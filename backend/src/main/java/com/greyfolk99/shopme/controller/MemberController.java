package com.greyfolk99.shopme.controller;


import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.dto.form.MemberForm;
import com.greyfolk99.shopme.dto.form.MemberUpdateForm;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.RestControllerException;
import com.greyfolk99.shopme.exception.rest.ValidationFailedException;
import com.greyfolk99.shopme.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/member")
    public String memberPage(){
        return "member/myPage";
    }

    @GetMapping("/member/edit")
    public String updateMemberPage(
            Principal principal,
            Model model
    ) {
        Member member = (Member) memberService.loadUserByUsername(principal.getName());
        MemberUpdateForm memberUpdateForm = memberService.getMemberFormForUpdate(member);

        model.addAttribute("isSocialMember", principal instanceof OAuth2AuthenticationToken);
        model.addAttribute("memberForm", memberUpdateForm);

        return "member/memberForm";
    }

    @PostMapping("/member/edit")
    public String updateMember(
            @Valid MemberUpdateForm memberUpdateForm,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        log.info("[updateMember] memberUpdateForm : {}", memberUpdateForm);
        Member member = (Member) memberService.loadUserByUsername(principal.getName());
        model.addAttribute("isSocialMember", principal instanceof OAuth2AuthenticationToken);

        // 소셜 회원이면 주소만 수정 가능
        if (principal instanceof OAuth2AuthenticationToken && !bindingResult.hasFieldErrors("address")) {
            memberService.updateAddress(member, memberUpdateForm.getAddress());
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("memberForm", memberUpdateForm);
            return "member/memberForm";
        }

        try {
            if (Objects.equals(memberUpdateForm.getEmail(), member.getEmail())) {
                throw new ValidationFailedException(ExceptionClass.MEMBER, "이메일은 변경할 수 없습니다.");
            }
            memberService.updateMember(member, memberUpdateForm, passwordEncoder);
        }

        catch (RestControllerException ex){
            model.addAttribute("exception", ex.getMessage());
            log.error("[updateMember] unexpected exception occurred : {}", ex.toString());
            return "member/memberForm";
        }

        catch (Exception e) {
            model.addAttribute("exception", "회원정보 수정에 실패했습니다.");
            log.error("[updateMember] unexpected exception occurred : {}", e.toString());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> deleteMember(
            HttpServletRequest request,
            Principal principal
    ) {

        Member member = (Member) memberService.loadUserByUsername(principal.getName());
        memberService.deleteMember(member);

        // Invalidate the current session
        request.getSession().invalidate();

        // Clear the remember-me cookie
        Cookie cookie = new Cookie("remember-me", null);
        cookie.setMaxAge(0);

        SecurityContextHolder.clearContext();

        Map<String, String> body = new HashMap<>();
        body.put("message", "회원탈퇴가 완료되었습니다.");

        return ResponseEntity.ok(body);
    }
}

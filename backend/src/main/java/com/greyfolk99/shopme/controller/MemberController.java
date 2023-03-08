package com.greyfolk99.shopme.controller;


import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.dto.form.MemberForm;
import com.greyfolk99.shopme.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
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
        MemberForm memberForm = memberService.getMemberFormForUpdate(member);
        model.addAttribute("memberForm", memberForm);
        return "member/memberForm";
    }

    @PostMapping("/member/edit")
    public String updateMember(
            @Valid MemberForm memberForm,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        Member member = (Member) memberService.loadUserByUsername(principal.getName());

        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        if (!memberForm.getEmail().equals(member.getUsername())) {
            bindingResult.addError(
                new FieldError("email", "email", "이메일은 변경이 불가능합니다."));
            return "member/memberForm";
        }

        try {
            memberService.updateMember(member, memberForm, passwordEncoder);
        }

        catch (Exception e) {
            model.addAttribute("exception", "업데이트에 실패했습니다.");
            log.error("[updateMember] unexpected exception occurred : {}", e.toString());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> deleteMember(
            Principal principal
    ) {
        Member member = (Member) memberService.loadUserByUsername(principal.getName());
        SecurityContextHolder.clearContext();
        memberService.deleteMember(member);
        return ResponseEntity.ok().build();
    }
}

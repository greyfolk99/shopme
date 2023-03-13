package com.greyfolk99.shopme.controller;

import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.dto.form.MemberForm;
import com.greyfolk99.shopme.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    @GetMapping("/auth/login")
    public String memberLogin(
        HttpServletRequest request,
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "exception", required = false) String exception,
        Model model
    ) {
        String referer = request.getHeader("Referer");
        if(referer != null){
            request.getSession().setAttribute("prevPage", referer);
        }
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "member/loginForm";
    }

    // 회원가입 페이지
    @GetMapping("/auth/join")
    public String memberJoinForm(
        MemberForm memberForm,
        Model model
    ) {
        model.addAttribute("memberForm", memberForm);
        return "member/memberForm";
    }

    // 회원가입
    @PostMapping("/auth/join")
    public String memberJoin(
            @Valid MemberForm memberForm,
            BindingResult bindingResult,
            Model model
    ) {
        // validation 실패 시
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }
        // join
        try {
            Member member = memberService.saveMember(memberForm, passwordEncoder);
            log.info("회원가입 성공: " + member.getUsername());
        } catch (Exception e) {
            model.addAttribute("exception", e.getMessage());
            log.error(e.toString());
            e.printStackTrace();
            return "member/memberForm";
        }
        return "redirect:/"; // PRG 패턴
    }
}

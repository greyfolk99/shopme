package com.greyfolk99.shopme.service;

import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.dto.form.MemberForm;
import com.greyfolk99.shopme.exception.ExceptionClass;
import com.greyfolk99.shopme.exception.rest.ResourceExistException;
import com.greyfolk99.shopme.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;

    public Optional<Member> findMember(String email) {
        return memberRepository.findByEmail(email);}

    // UserDetails로 Member 영속화
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return findMember(email).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
    }

    // 회원가입
    public Member saveMember(MemberForm memberForm, PasswordEncoder passwordEncoder) {
        Member member = Member.from(memberForm, passwordEncoder);
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }
    public Member saveOAuthMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    // 업데이트용 Form 가져오기
    public MemberForm getMemberFormForUpdate(Member member){
        return MemberForm.forUpdate(member);
    }

    // 회원 업데이트
    public void updateMember(Member member, MemberForm memberForm, PasswordEncoder passwordEncoder) {
        member.update(memberForm, passwordEncoder);
    }

    // 이메일 중복 검증
    private void validateDuplicateMember(Member member) {
        findMember(member.getEmail())
            .ifPresent(val-> {throw new ResourceExistException(ExceptionClass.MEMBER, "이미 존재하는 이메일 주소입니다.");});
    }

    public void deleteMember(Member member) {
        memberRepository.delete(member);
        if (findMember(member.getEmail()).isPresent()) {
            throw new ResourceExistException(ExceptionClass.MEMBER, "회원 삭제에 실패했습니다.");
        }
    }
}

package com.greyfolk99.shopme.dto.form;

import com.greyfolk99.shopme.domain.member.Address;
import com.greyfolk99.shopme.domain.member.Member;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateForm {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;
    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordRe;
    @Valid
    private Address address;

    @AssertTrue(message = "비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordReValid(){
        return Objects.equals(password, passwordRe);
    }

    public static MemberUpdateForm of(Member member){
        return MemberUpdateForm.builder()
            .name(member.getName())
            .email(member.getEmail())
            .address(member.getAddress())
            .build();
    }
}

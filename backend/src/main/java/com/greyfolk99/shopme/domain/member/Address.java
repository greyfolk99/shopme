package com.greyfolk99.shopme.domain.member;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @NotBlank(message = "우편번호는 필수 입력 값입니다.")
    private String zipcode;
    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address1;
    @NotBlank(message = "상세 주소는 필수 입력 값입니다.")
    private String address2;
    private String address3;

    public void update(Address address){
        zipcode = address.getZipcode();
        address1 = address.getAddress1();
        address2 = address.getAddress2();
        address3 = address.getAddress3();
    }
}

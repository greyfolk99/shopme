package com.greyfolk99.shopme.service;

import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.domain.member.Role;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;

    // Member 생성을 위한 필드
    private String username; // email + platform
    private String name;
    private Boolean isValidate;

    OAuthAttributes (String registrationId, String nameAttributeKey, Map<String, Object> attributes) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }


    // 해당 로그인인 서비스가 kakao인지 google인지 구분하여, 알맞게 매핑을 해주도록 합니다.
    // 여기서 registrationId는 OAuth2 로그인을 처리한 서비스 명("google","kakao","naver"..)이 되고,
    // userNameAttributeName은 해당 서비스의 map의 키값이 되는 값이됩니다. {google="sub", kakao="id", naver="response"}
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if (registrationId.equals("kakao")) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    // https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#profile
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");  // 카카오로 받은 데이터에서 계정 정보가 담긴 kakao_account 값을 꺼낸다.
        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");

        String username = (String) kakao_account.get("email") + "_kakao";
        String name = (String) kakao_account.get("profile_nickname");
        Boolean isValidate = (Boolean) kakao_account.get("is_email_valid") && (Boolean) kakao_account.get("is_email_verified");

        return new OAuthAttributes(attributes, userNameAttributeName, username, name, isValidate);
    }


    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

        String username = (String) attributes.get("email") + "_google";
        String name = (String) attributes.get("name");

        return new OAuthAttributes(attributes, userNameAttributeName, username, name, true);
    }

    // .. getter/setter 생략

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.of(
            username,
            passwordEncoder.encode(UUID.randomUUID().toString()),
            name,
            Role.MEMBER,
            username);
    }
}

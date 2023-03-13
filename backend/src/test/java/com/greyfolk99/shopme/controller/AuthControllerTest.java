package com.greyfolk99.shopme.controller;

import com.greyfolk99.shopme.config.SecurityConfig;
import com.greyfolk99.shopme.domain.member.Address;
import com.greyfolk99.shopme.domain.member.Member;
import com.greyfolk99.shopme.domain.member.Role;
import com.greyfolk99.shopme.service.MemberService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Before @Test
    public void setUp() {
        Member member = Member.of(
            UUID.randomUUID(),
            "test@test.com",
            "test",
            Address.builder()
                .address1("test").address2("test").zipcode("12345").build(),
            "1234567890",
            Role.MEMBER
        );
    }

    @Test
    public void givenLoginWithRememberMe_thenAuthenticated() throws Exception {
        // Perform login with remember-me enabled
        MvcResult result = mockMvc.perform(
            post("/auth/login").with(csrf())
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("username", "test@test.com")
            .param("password", "1234567890")
            .param("remember-me", "true"))
        .andReturn();

        // Check for the presence of the "myCookie" cookie
        Cookie myCookie = result.getResponse().getCookie("JSESSIONID");
        assertNotNull(myCookie);
    }
}

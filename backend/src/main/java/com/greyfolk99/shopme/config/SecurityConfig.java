package com.greyfolk99.shopme.config;

import com.greyfolk99.shopme.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberService memberService;

    @Value("${server.remember-me-key}")
    private String tokenKey;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return
    http
        .csrf()
            .and()
        .authorizeHttpRequests( auth -> auth
                .antMatchers("/css/**", "/js/**", "/image/**").permitAll()
                .antMatchers("/admin*/**").hasRole("ADMIN")
                .antMatchers("/order*/**", "/member*/**").hasAnyRole("ADMIN", "MEMBER")
                .antMatchers("/", "/auth/join","/auth/login", "/item/*", "/cart", "/cart/api/item")
                .permitAll()
                .anyRequest().authenticated()
                .and())
        .userDetailsService(memberService)
        .rememberMe()
            .key(tokenKey)
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(60 * 60 * 24 * 7)
            .userDetailsService(memberService)
            .and()
        .formLogin()
            .loginPage("/auth/login")
            .defaultSuccessUrl("/")
            .usernameParameter("email")
            .passwordParameter("password")
            .failureHandler(loginFailHandler())
            .successHandler(successHandler())
            .and()
        .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
            .authenticationEntryPoint(authenticationEntryPoint())
            .and()
        .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
            .logoutSuccessUrl("/")
            .and()

        .sessionManagement(configurer -> configurer
            .maximumSessions(5)
            .maxSessionsPreventsLogin(true)
            .expiredUrl("/?error=true&exception=" +
                    URLEncoder.encode("????????? ?????????????????????.", StandardCharsets.UTF_8)))
        .build();
    }

    private AuthenticationFailureHandler loginFailHandler() {
        return (request, response, exception) -> {
            String exceptionMessage;
            if (exception instanceof BadCredentialsException) {
                exceptionMessage = "????????? ?????? ??????????????? ?????? ????????????. ?????? ????????? ?????????.";
            } else if (exception instanceof InternalAuthenticationServiceException) {
                exceptionMessage = "??????????????? ????????? ????????? ????????? ?????? ????????? ????????? ??? ????????????. ??????????????? ???????????????.";
            } else if (exception instanceof UsernameNotFoundException) {
                exceptionMessage = "????????? ???????????? ????????????. ???????????? ?????? ??? ????????? ????????????.";
            } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
                exceptionMessage = "?????? ????????? ?????????????????????. ??????????????? ???????????????.";
            } else {
                System.out.println(exception.getMessage());
                exceptionMessage = "??? ??? ?????? ????????? ???????????? ????????????????????? ??????????????? ???????????????.";
            }
            exceptionMessage = URLEncoder.encode(exceptionMessage, StandardCharsets.UTF_8);
            response.sendRedirect("/auth/login?error=true&exception=" + exceptionMessage);
        };
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> {
            String exceptionMessage = URLEncoder.encode(
                    "?????? ????????? ????????????.", StandardCharsets.UTF_8);
            if (request.getRequestURI().contains("/api")) response.sendError(HttpServletResponse.SC_FORBIDDEN,exceptionMessage);
            else response.sendRedirect("/?error=true&exception=" + exceptionMessage);
        };
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, exception) -> {
            String exceptionMessage = URLEncoder.encode(
                    "???????????? ????????? ??????????????????.", StandardCharsets.UTF_8);
            if (request.getRequestURI().contains("/api")) response.sendError(HttpServletResponse.SC_UNAUTHORIZED,exceptionMessage);
            else response.sendRedirect("/auth/login?error=true&exception=" + exceptionMessage);
        };
    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            HttpSession session = request.getSession();
            System.out.println("someone logged in at " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").localizedBy(Locale.KOREA)));
            if (session == null) {response.sendRedirect("/"); return;}
            String redirectUrl = (String) session.getAttribute("prevPage");
            if (redirectUrl == null) {response.sendRedirect("/"); return;}
            session.removeAttribute("prevPage");
            if (redirectUrl.contains("/auth") || redirectUrl.contains("/member/logout")) {
                response.sendRedirect("/"); return;
            }
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}


package com.greyfolk99.shopme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

/*
    * 기본 페이지를 1로 설정
 */
@Configuration
public class PageConfig {
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        return pageableResolver -> {
            pageableResolver.setOneIndexedParameters(true);
        };
    }
}

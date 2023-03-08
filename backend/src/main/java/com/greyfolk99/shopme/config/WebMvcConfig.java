package com.greyfolk99.shopme.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${resource.image.path}")
    String imageResourceLocation;

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {

        WebMvcConfigurer.super.addResourceHandlers(registry);
        //Swagger UI property
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/image/**")
                .addResourceLocations(getSystemFileRoot() + Paths.get(imageResourceLocation, "image") + "/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }

    private String getSystemFileRoot(){
        String path = System.getProperty("os.name").toLowerCase().contains("win") ?
                "file:///C:" : // Window 환경
                "file://"; // 그 외
        System.out.println(path + Paths.get(imageResourceLocation, "image") + "/");
        return path;
    }
}

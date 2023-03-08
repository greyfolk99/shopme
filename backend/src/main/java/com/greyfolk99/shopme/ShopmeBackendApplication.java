package com.greyfolk99.shopme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan // scan external configuration
@SpringBootApplication
public class ShopmeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopmeBackendApplication.class, args);
    }

}

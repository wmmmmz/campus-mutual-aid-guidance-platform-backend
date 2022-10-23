package com.wmz.campusplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class CampusPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusPlatformApplication.class, args);
    }

}

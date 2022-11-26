package com.wmz.campusplatform;

import org.apache.logging.log4j.core.config.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class CampusPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusPlatformApplication.class, args);
    }

}

package com.damian.coderover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CodeRoverSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeRoverSpringApplication.class, args);
    }

}

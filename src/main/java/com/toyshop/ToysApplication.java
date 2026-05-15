package com.toyshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.toyshop.**.mapper")
public class ToysApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToysApplication.class, args);
    }
}


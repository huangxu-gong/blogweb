package com.example.blogweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.example.blogweb.mapper")
@SpringBootApplication
public class BlogwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogwebApplication.class, args);
    }

}

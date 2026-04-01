package com.gilbertoparente.library.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.gilbertoparente.library")
@EnableJpaRepositories(basePackages = "com.gilbertoparente.library.repositories")
@EntityScan(basePackages = "com.gilbertoparente.library.entities")
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
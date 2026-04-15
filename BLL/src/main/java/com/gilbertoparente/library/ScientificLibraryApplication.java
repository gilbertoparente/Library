package com.gilbertoparente.library;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ScientificLibraryApplication {

    public static void main(String[] args) {
        // Inicia Spring e Hibernate
        SpringApplication.run(ScientificLibraryApplication.class, args);
    }

}
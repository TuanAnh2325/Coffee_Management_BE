package com.coffeeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
@EnableCaching
public class CoffeeManagementApplication {

    public static void main(String[] args) {

        SpringApplication.run(CoffeeManagementApplication.class, args);
    }

}

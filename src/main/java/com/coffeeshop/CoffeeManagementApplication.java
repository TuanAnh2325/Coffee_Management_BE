package com.coffeeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
public class CoffeeManagementApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(CoffeeManagementApplication.class, args);
    }

    private static void loadEnv() {
        File envFile = new File(".env");
        if (envFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(envFile.toPath());
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                        int idx = line.indexOf('=');
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();
                        System.setProperty(key, value);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}

package com.coffeeshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        File dir = uploadDir.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String uploadPath = uploadDir.toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath.endsWith("/") ? uploadPath : uploadPath + "/");
    }
}

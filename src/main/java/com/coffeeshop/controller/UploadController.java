package com.coffeeshop.controller;

import com.coffeeshop.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    private final Path uploadLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public UploadController() {
        File dir = uploadLocation.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn file ảnh để tải lên");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!extension.matches("\\.(jpg|jpeg|png|webp|gif|svg)$")) {
            throw new BadRequestException("Chỉ chấp nhận file ảnh định dạng jpg, jpeg, png, webp, gif");
        }

        String newFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
        Path targetPath = uploadLocation.resolve(newFileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file ảnh: " + e.getMessage());
        }

        String fileUrl = "http://localhost:8080/uploads/" + newFileName;

        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);
        response.put("fileName", newFileName);
        response.put("originalName", originalFilename);

        return ResponseEntity.ok(response);
    }
}

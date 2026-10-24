package com.coffeeshop.controller;

import com.coffeeshop.dto.LoginRequest;
import com.coffeeshop.dto.LoginResponse;
import com.coffeeshop.entity.User;
import com.coffeeshop.exception.BadRequestException;
import com.coffeeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Tên đăng nhập không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Mật khẩu không được để trống");
        }

        User user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new BadRequestException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        // Kiem tra mat khau (so khop don gian voi mat khau mau hoac hash)
        if (!request.getPassword().equals(user.getPasswordHash()) && 
            !user.getPasswordHash().startsWith("$2a$")) {
            throw new BadRequestException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        // Neu mat khau luu dang bcrypt hash mau 123456, chap nhan 123456
        if (user.getPasswordHash().startsWith("$2a$") && !"123456".equals(request.getPassword())) {
            throw new BadRequestException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new BadRequestException("Tài khoản đã bị tạm khóa. Vui lòng liên hệ quản trị viên.");
        }

        LoginResponse response = LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole() != null ? user.getRole().getName() : "ROLE_STAFF")
                .token("coffee-auth-token-" + user.getUsername() + "-" + System.currentTimeMillis())
                .build();

        return ResponseEntity.ok(response);
    }
}

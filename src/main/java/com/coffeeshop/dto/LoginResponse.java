package com.coffeeshop.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String fullName;
    private String role;
    private String token;
}

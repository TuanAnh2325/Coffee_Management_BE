package com.coffeeshop.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String email;
    private String roleName;
    private Boolean isActive;
}

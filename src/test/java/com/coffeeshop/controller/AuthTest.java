package com.coffeeshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test dang nhap Admin thanh cong -> ROLE_ADMIN")
    void testAdminLoginSuccess() throws Exception {
        String json = """
            {
                "username": "admin",
                "password": "123456"
            }
        """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("Test dang nhap Staff thanh cong -> ROLE_STAFF")
    void testStaffLoginSuccess() throws Exception {
        String json = """
            {
                "username": "staff",
                "password": "123456"
            }
        """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("staff"))
                .andExpect(jsonPath("$.role").value("ROLE_STAFF"))
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    @DisplayName("Test dang nhap sai mat khau -> 400 Bad Request")
    void testLoginWrongPassword() throws Exception {
        String json = """
            {
                "username": "admin",
                "password": "wrong_password_999"
            }
        """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("không chính xác")));
    }
}

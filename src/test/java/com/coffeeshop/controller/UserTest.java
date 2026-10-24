package com.coffeeshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/users tra ve danh sach nhan vien")
    void testGetUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[?(@.username == 'admin')].fullName", hasItem("Quản trị viên")))
                .andExpect(jsonPath("$[?(@.username == 'staff')].roleName", hasItem("ROLE_STAFF")));
    }

    @Test
    @DisplayName("GET /api/reports/month tra ve bao cao doanh thu thang")
    void testGetMonthReport() throws Exception {
        mockMvc.perform(get("/api/reports/month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year", notNullValue()))
                .andExpect(jsonPath("$.month", notNullValue()))
                .andExpect(jsonPath("$.monthLabel", containsString("Tháng")))
                .andExpect(jsonPath("$.dailyStats", not(empty())))
                .andExpect(jsonPath("$.totalRevenue", notNullValue()));
    }
}

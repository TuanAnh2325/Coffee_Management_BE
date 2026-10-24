package com.coffeeshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test loi: Tim san pham khong ton tai (ID = 9999) -> 404 Not Found")
    void testGetNonExistentProduct() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy sản phẩm với ID: 9999")));
    }

    @Test
    @DisplayName("Test thanh cong: Tim san pham co that (ID = 1) -> 200 OK")
    void testGetValidProduct() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("CF-DEN"))
                .andExpect(jsonPath("$.name", notNullValue()));
    }
}

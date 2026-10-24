package com.coffeeshop.controller;

import com.coffeeshop.entity.Category;
import com.coffeeshop.entity.Product;
import com.coffeeshop.repository.CategoryRepository;
import com.coffeeshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long testProductId;

    @BeforeEach
    void setUp() {
        if (productRepository.count() == 0) {
            Category category = categoryRepository.findByCode("COFFEE").orElseGet(() ->
                    categoryRepository.save(Category.builder()
                            .code("COFFEE")
                            .name("Cà phê")
                            .description("Cà phê truyền thống")
                            .isActive(true)
                            .build())
            );
            Product product = productRepository.save(Product.builder()
                    .category(category)
                    .code("CF-DEN")
                    .name("Cà phê Đen đá")
                    .basePrice(new BigDecimal("25000"))
                    .isAvailable(true)
                    .build());
            testProductId = product.getId();
        } else {
            testProductId = productRepository.findAll().get(0).getId();
        }
    }

    @Test
    @DisplayName("Test loi: Tim san pham khong ton tai (ID = 9999) -> 404 Not Found")
    void testGetNonExistentProduct() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy sản phẩm với ID: 9999")));
    }

    @Test
    @DisplayName("Test thanh cong: Tim san pham co that -> 200 OK")
    void testGetValidProduct() throws Exception {
        mockMvc.perform(get("/api/products/" + testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProductId))
                .andExpect(jsonPath("$.code", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()));
    }
}

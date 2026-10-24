package com.coffeeshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UploadTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test upload anh hop le -> 200 OK")
    void testUploadValidImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                new byte[]{1, 2, 3, 4}
        );

        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", containsString("http://localhost:8080/uploads/")))
                .andExpect(jsonPath("$.fileName", notNullValue()));
    }
}

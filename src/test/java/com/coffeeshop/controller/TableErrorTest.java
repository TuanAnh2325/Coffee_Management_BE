package com.coffeeshop.controller;

import com.coffeeshop.entity.DiningTable;
import com.coffeeshop.repository.DiningTableRepository;
import org.junit.jupiter.api.BeforeEach;
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
class TableErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiningTableRepository tableRepository;

    private Integer testTableId;

    @BeforeEach
    void setUp() {
        if (tableRepository.count() == 0) {
            DiningTable table = tableRepository.save(DiningTable.builder()
                    .tableNumber("Bàn 01")
                    .area("Tầng 1")
                    .status("EMPTY")
                    .capacity(4)
                    .build());
            testTableId = table.getId();
        } else {
            testTableId = tableRepository.findAll().get(0).getId();
        }
    }

    @Test
    @DisplayName("Test loi: Tim ban khong ton tai (ID = 999) -> 404 Not Found")
    void testGetNonExistentTable() throws Exception {
        mockMvc.perform(get("/api/tables/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy bàn với ID: 999")));
    }

    @Test
    @DisplayName("Test loi: Doi trang thai ban thanh gia tri bat hop le -> 400 Bad Request")
    void testUpdateTableWithInvalidStatus() throws Exception {
        String json = """
            {
                "status": "HONG_HOC_KHONG_DUNG_DUOC"
            }
        """;

        mockMvc.perform(put("/api/tables/" + testTableId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Trạng thái bàn không hợp lệ")));
    }

    @Test
    @DisplayName("Test thanh cong: Doi trang thai ban hop le sang SERVING -> 200 OK")
    void testUpdateTableStatusSuccess() throws Exception {
        String json = """
            {
                "status": "SERVING"
            }
        """;

        mockMvc.perform(put("/api/tables/" + testTableId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SERVING"));
    }
}

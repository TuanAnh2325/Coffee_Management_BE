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
class OrderErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test loi: Don hang khong co mon nao (items rong) -> 400 Bad Request")
    void testCreateOrderWithEmptyItems() throws Exception {
        String json = """
            {
                "orderType": "TAKE_AWAY",
                "paymentMethod": "CASH",
                "items": []
            }
        """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Đơn hàng phải có ít nhất 1 món")));
    }

    @Test
    @DisplayName("Test loi: Dat mon voi so luong am (quantity = -3) -> 400 Bad Request")
    void testCreateOrderWithNegativeQuantity() throws Exception {
        String json = """
            {
                "orderType": "TAKE_AWAY",
                "paymentMethod": "CASH",
                "items": [
                    {
                        "productId": 1,
                        "productName": "Cà phê Đen đá",
                        "quantity": -3,
                        "unitPrice": 25000
                    }
                ]
            }
        """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Số lượng món phải lớn hơn 0")));
    }

    @Test
    @DisplayName("Test loi: Dat mon voi so luong bang 0 -> 400 Bad Request")
    void testCreateOrderWithZeroQuantity() throws Exception {
        String json = """
            {
                "orderType": "TAKE_AWAY",
                "paymentMethod": "CASH",
                "items": [
                    {
                        "productId": 2,
                        "productName": "Cà phê Sữa đá",
                        "quantity": 0,
                        "unitPrice": 29000
                    }
                ]
            }
        """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Số lượng món phải lớn hơn 0")));
    }

    @Test
    @DisplayName("Test loi: Don tai quan (DINE_IN) nhung quen chon ban -> 400 Bad Request")
    void testCreateDineInOrderWithoutTable() throws Exception {
        String json = """
            {
                "orderType": "DINE_IN",
                "tableId": null,
                "paymentMethod": "CASH",
                "items": [
                    {
                        "productId": 1,
                        "quantity": 2
                    }
                ]
            }
        """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("bắt buộc phải chọn bàn")));
    }

    @Test
    @DisplayName("Test loi: Phuong thuc thanh toan khong hop le -> 400 Bad Request")
    void testCreateOrderWithInvalidPaymentMethod() throws Exception {
        String json = """
            {
                "orderType": "TAKE_AWAY",
                "paymentMethod": "CRYPTO_USDT",
                "items": [
                    {
                        "productId": 1,
                        "quantity": 1
                    }
                ]
            }
        """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Phương thức thanh toán không hợp lệ")));
    }

    @Test
    @DisplayName("Test loi: Tim don hang khong ton tai (ID = 999999) -> 404 Not Found")
    void testGetNonExistentOrder() throws Exception {
        mockMvc.perform(get("/api/orders/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy đơn hàng với ID: 999999")));
    }

    @Test
    @DisplayName("Test thanh cong: Tao don hop le -> 200 OK")
    void testCreateValidOrder() throws Exception {
        String json = """
            {
                "orderType": "DINE_IN",
                "tableId": 1,
                "paymentMethod": "CASH",
                "totalAmount": 58000,
                "items": [
                    {
                        "productId": 1,
                        "productName": "Cà phê Đen đá",
                        "sizeName": "M",
                        "quantity": 2,
                        "unitPrice": 29000
                    }
                ]
            }
        """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCode", notNullValue()))
                .andExpect(jsonPath("$.orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }
}

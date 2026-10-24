package com.coffeeshop.controller;

import com.coffeeshop.entity.*;
import com.coffeeshop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiningTableRepository tableRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    private Integer testTableId;
    private Long testProductId;

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

            inventoryItemRepository.save(InventoryItem.builder()
                    .product(product)
                    .quantityInStock(100)
                    .minStockThreshold(10)
                    .unit("ly")
                    .build());
        } else {
            Product product = productRepository.findAll().get(0);
            testProductId = product.getId();
            if (inventoryItemRepository.findByProductId(testProductId).isEmpty()) {
                inventoryItemRepository.save(InventoryItem.builder()
                        .product(product)
                        .quantityInStock(100)
                        .minStockThreshold(10)
                        .unit("ly")
                        .build());
            }
        }
    }

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
        String json = String.format("""
            {
                "orderType": "TAKE_AWAY",
                "paymentMethod": "CASH",
                "items": [
                    {
                        "productId": %d,
                        "productName": "Cà phê Đen đá",
                        "quantity": -3,
                        "unitPrice": 25000
                    }
                ]
            }
        """, testProductId);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Số lượng món phải lớn hơn 0")));
    }

    @Test
    @DisplayName("Test loi: Dat mon voi so luong = 0 -> 400 Bad Request")
    void testCreateOrderWithZeroQuantity() throws Exception {
        String json = String.format("""
            {
                "orderType": "TAKE_AWAY",
                "paymentMethod": "CASH",
                "items": [
                    {
                        "productId": %d,
                        "productName": "Cà phê Đen đá",
                        "quantity": 0,
                        "unitPrice": 25000
                    }
                ]
            }
        """, testProductId);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Số lượng món phải lớn hơn 0")));
    }

    @Test
    @DisplayName("Test loi: Don hang tai quan (DINE_IN) nhung quen chon ban (tableId = null) -> 400 Bad Request")
    void testCreateDineInOrderWithoutTable() throws Exception {
        String json = String.format("""
            {
                "orderType": "DINE_IN",
                "tableId": null,
                "paymentMethod": "CASH",
                "items": [
                    {
                        "productId": %d,
                        "quantity": 1,
                        "unitPrice": 25000
                    }
                ]
            }
        """, testProductId);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Đơn hàng phục vụ tại quán bắt buộc phải chọn bàn")));
    }

    @Test
    @DisplayName("Test loi: Don hang tai quan voi ban khong ton tai (tableId = 99999) -> 404 Not Found")
    void testCreateDineInOrderWithNonExistentTable() throws Exception {
        String json = String.format("""
            {
                "orderType": "DINE_IN",
                "tableId": 99999,
                "paymentMethod": "CASH",
                "items": [
                    {
                        "productId": %d,
                        "quantity": 1,
                        "unitPrice": 25000
                    }
                ]
            }
        """, testProductId);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Không tìm thấy bàn với ID: 99999")));
    }

    @Test
    @DisplayName("Test loi: Phuong thuc thanh toan khong hop le (paymentMethod = BITCOIN) -> 400 Bad Request")
    void testCreateOrderWithInvalidPaymentMethod() throws Exception {
        String json = String.format("""
            {
                "orderType": "TAKE_AWAY",
                "paymentMethod": "BITCOIN",
                "items": [
                    {
                        "productId": %d,
                        "quantity": 1,
                        "unitPrice": 25000
                    }
                ]
            }
        """, testProductId);

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
        String json = String.format("""
            {
                "orderType": "DINE_IN",
                "tableId": %d,
                "paymentMethod": "CASH",
                "totalAmount": 58000,
                "items": [
                    {
                        "productId": %d,
                        "productName": "Cà phê Đen đá",
                        "sizeName": "M",
                        "quantity": 2,
                        "unitPrice": 29000
                    }
                ]
            }
        """, testTableId, testProductId);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderCode", notNullValue()))
                .andExpect(jsonPath("$.orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }
}

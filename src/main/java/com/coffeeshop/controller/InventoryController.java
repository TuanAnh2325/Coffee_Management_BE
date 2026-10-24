package com.coffeeshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private static final List<Map<String, Object>> MOCK_INVENTORY = new ArrayList<>();

    static {
        addInv(1L, "Cà phê Đen đá", 150, 20, "Ly", "AN_TOAN");
        addInv(2L, "Cà phê Sữa đá", 120, 20, "Ly", "AN_TOAN");
        addInv(3L, "Bạc xỉu 3 tầng", 95, 20, "Ly", "AN_TOAN");
        addInv(4L, "Trà Đào Cam Sả", 80, 15, "Ly", "AN_TOAN");
        addInv(5L, "Trà Vải Hoa Lài", 12, 15, "Ly", "SAP_HET");
        addInv(6L, "Matcha Đá Xay Thạch", 45, 10, "Ly", "AN_TOAN");
        addInv(7L, "Bánh Tiramisu Ý", 8, 10, "Chiếc", "SAP_HET");
    }

    private static void addInv(Long productId, String productName, int stock, int minThreshold, String unit, String status) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("productId", productId);
        item.put("productName", productName);
        item.put("quantityInStock", stock);
        item.put("minStockThreshold", minThreshold);
        item.put("unit", unit);
        item.put("status", status);
        MOCK_INVENTORY.add(item);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getInventory() {
        return ResponseEntity.ok(MOCK_INVENTORY);
    }
}

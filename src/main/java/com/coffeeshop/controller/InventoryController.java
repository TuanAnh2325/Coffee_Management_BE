package com.coffeeshop.controller;

import com.coffeeshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> updateStock(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> payload) {
        Integer quantity = payload != null ? payload.get("quantity") : null;
        if (quantity == null || quantity < 0) {
            throw new com.coffeeshop.exception.BadRequestException("Số lượng tồn kho phải >= 0");
        }
        inventoryService.updateStock(productId, quantity);
        return ResponseEntity.ok(Map.of("message", "Cập nhật kho thành công", "productId", productId, "quantity", quantity));
    }
}

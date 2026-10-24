package com.coffeeshop.service;

import com.coffeeshop.entity.InventoryItem;
import com.coffeeshop.entity.Product;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.repository.InventoryItemRepository;
import com.coffeeshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ProductRepository productRepository;

    public List<Map<String, Object>> getAllInventory() {
        List<InventoryItem> items = inventoryItemRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (InventoryItem item : items) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            map.put("productId", item.getProduct().getId());
            map.put("productName", item.getProduct().getName());
            map.put("quantityInStock", item.getQuantityInStock());
            map.put("minStockThreshold", item.getMinStockThreshold());
            map.put("unit", item.getUnit());
            map.put("status", item.getQuantityInStock() <= item.getMinStockThreshold() ? "SAP_HET" : "AN_TOAN");
            result.add(map);
        }
        return result;
    }

    @Transactional
    public InventoryItem updateStock(Long productId, Integer quantity) {
        InventoryItem item = inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho của sản phẩm ID: " + productId));
        item.setQuantityInStock(quantity);
        return inventoryItemRepository.save(item);
    }
}

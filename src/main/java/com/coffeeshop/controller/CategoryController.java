package com.coffeeshop.controller;

import com.coffeeshop.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private static final List<Map<String, Object>> MOCK_CATEGORIES = new ArrayList<>();

    static {
        Map<String, Object> c1 = new LinkedHashMap<>();
        c1.put("id", 1);
        c1.put("code", "COFFEE");
        c1.put("name", "Cà phê truyền thống & Pha máy");
        c1.put("description", "Cà phê Robusta & Arabica nguyên chất thơm đậm đà");
        c1.put("isActive", true);
        MOCK_CATEGORIES.add(c1);

        Map<String, Object> c2 = new LinkedHashMap<>();
        c2.put("id", 2);
        c2.put("code", "TEA");
        c2.put("name", "Trà trái cây nhiệt đới");
        c2.put("description", "Trà hoa quả thanh mát kết hợp trái cây tươi giòn ngọt");
        c2.put("isActive", true);
        MOCK_CATEGORIES.add(c2);

        Map<String, Object> c3 = new LinkedHashMap<>();
        c3.put("id", 3);
        c3.put("code", "FREEZE");
        c3.put("name", "Đá xay & Sinh tố");
        c3.put("description", "Thức uống đá xay béo ngậy kèm kem tươi whipping");
        c3.put("isActive", true);
        MOCK_CATEGORIES.add(c3);

        Map<String, Object> c4 = new LinkedHashMap<>();
        c4.put("id", 4);
        c4.put("code", "CAKE");
        c4.put("name", "Bánh ngọt & Điểm tâm");
        c4.put("description", "Bánh nướng tươi mỗi ngày ăn kèm đồ uống");
        c4.put("isActive", true);
        MOCK_CATEGORIES.add(c4);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        return ResponseEntity.ok(MOCK_CATEGORIES);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Integer id) {
        return MOCK_CATEGORIES.stream()
                .filter(c -> c.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));
    }
}

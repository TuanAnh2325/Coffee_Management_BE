package com.coffeeshop.controller;

import com.coffeeshop.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private static final List<Map<String, Object>> MOCK_PRODUCTS = new ArrayList<>();
    private static final List<Map<String, Object>> MOCK_SIZES = new ArrayList<>();

    static {
        Map<String, Object> s1 = new LinkedHashMap<>();
        s1.put("id", 1);
        s1.put("sizeName", "S");
        s1.put("extraPrice", BigDecimal.ZERO);
        s1.put("description", "Cỡ nhỏ 350ml");
        MOCK_SIZES.add(s1);

        Map<String, Object> s2 = new LinkedHashMap<>();
        s2.put("id", 2);
        s2.put("sizeName", "M");
        s2.put("extraPrice", new BigDecimal("5000"));
        s2.put("description", "Cỡ vừa 500ml (+5.000đ)");
        MOCK_SIZES.add(s2);

        Map<String, Object> s3 = new LinkedHashMap<>();
        s3.put("id", 3);
        s3.put("sizeName", "L");
        s3.put("extraPrice", new BigDecimal("10000"));
        s3.put("description", "Cỡ lớn 700ml (+10.000đ)");
        MOCK_SIZES.add(s3);

        Map<String, Object> p1 = new LinkedHashMap<>();
        p1.put("id", 1L);
        p1.put("categoryId", 1);
        p1.put("categoryName", "Cà phê");
        p1.put("code", "CF-DEN");
        p1.put("name", "Cà phê Đen đá");
        p1.put("basePrice", new BigDecimal("25000"));
        p1.put("imageUrl", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400");
        p1.put("isAvailable", true);
        p1.put("description", "Cà phê Robusta đậm vị truyền thống");
        MOCK_PRODUCTS.add(p1);

        Map<String, Object> p2 = new LinkedHashMap<>();
        p2.put("id", 2L);
        p2.put("categoryId", 1);
        p2.put("categoryName", "Cà phê");
        p2.put("code", "CF-SUA");
        p2.put("name", "Cà phê Sữa đá");
        p2.put("basePrice", new BigDecimal("29000"));
        p2.put("imageUrl", "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=400");
        p2.put("isAvailable", true);
        p2.put("description", "Cà phê hòa quyện sữa đặc thơm ngọt béo");
        MOCK_PRODUCTS.add(p2);

        Map<String, Object> p3 = new LinkedHashMap<>();
        p3.put("id", 3L);
        p3.put("categoryId", 1);
        p3.put("categoryName", "Cà phê");
        p3.put("code", "BAC-XIU");
        p3.put("name", "Bạc xỉu 3 tầng");
        p3.put("basePrice", new BigDecimal("32000"));
        p3.put("imageUrl", "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=400");
        p3.put("isAvailable", true);
        p3.put("description", "Nhiều sữa ít cà phê, vị béo thơm thanh nhẹ");
        MOCK_PRODUCTS.add(p3);

        Map<String, Object> p4 = new LinkedHashMap<>();
        p4.put("id", 4L);
        p4.put("categoryId", 2);
        p4.put("categoryName", "Trà trái cây");
        p4.put("code", "TRA-DAO");
        p4.put("name", "Trà Đào Cam Sả");
        p4.put("basePrice", new BigDecimal("39000"));
        p4.put("imageUrl", "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400");
        p4.put("isAvailable", true);
        p4.put("description", "Trà đào thơm nồng kết hợp hương cam sả và miếng đào giòn");
        MOCK_PRODUCTS.add(p4);

        Map<String, Object> p5 = new LinkedHashMap<>();
        p5.put("id", 5L);
        p5.put("categoryId", 2);
        p5.put("categoryName", "Trà trái cây");
        p5.put("code", "TRA-VAI");
        p5.put("name", "Trà Vải Hoa Lài");
        p5.put("basePrice", new BigDecimal("39000"));
        p5.put("imageUrl", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400");
        p5.put("isAvailable", true);
        p5.put("description", "Trà lài thanh mát cùng quả vải ngâm ngọt mọng nước");
        MOCK_PRODUCTS.add(p5);

        Map<String, Object> p6 = new LinkedHashMap<>();
        p6.put("id", 6L);
        p6.put("categoryId", 3);
        p6.put("categoryName", "Đá xay");
        p6.put("code", "FREEZE-MATCHA");
        p6.put("name", "Matcha Đá Xay Thạch");
        p6.put("basePrice", new BigDecimal("45000"));
        p6.put("imageUrl", "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=400");
        p6.put("isAvailable", true);
        p6.put("description", "Trà xanh Nhật Bản đá xay phủ kem tươi và thạch matcha");
        MOCK_PRODUCTS.add(p6);

        Map<String, Object> p7 = new LinkedHashMap<>();
        p7.put("id", 7L);
        p7.put("categoryId", 4);
        p7.put("categoryName", "Bánh ngọt");
        p7.put("code", "CAKE-TIRAMISU");
        p7.put("name", "Bánh Tiramisu Ý");
        p7.put("basePrice", new BigDecimal("35000"));
        p7.put("imageUrl", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400");
        p7.put("isAvailable", true);
        p7.put("description", "Bánh mềm thơm hương cà phê và phô mai mascarpone");
        MOCK_PRODUCTS.add(p7);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getProducts(
            @RequestParam(required = false) Integer categoryId) {
        if (categoryId != null) {
            List<Map<String, Object>> filtered = MOCK_PRODUCTS.stream()
                    .filter(p -> categoryId.equals(p.get("categoryId")))
                    .toList();
            return ResponseEntity.ok(filtered);
        }
        return ResponseEntity.ok(MOCK_PRODUCTS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        return MOCK_PRODUCTS.stream()
                .filter(p -> p.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
    }

    @GetMapping("/sizes")
    public ResponseEntity<List<Map<String, Object>>> getProductSizes() {
        return ResponseEntity.ok(MOCK_SIZES);
    }
}

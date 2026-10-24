package com.coffeeshop.controller;

import com.coffeeshop.dto.ProductRequest;
import com.coffeeshop.entity.Product;
import com.coffeeshop.entity.ProductSize;
import com.coffeeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) Integer categoryId) {
        return ResponseEntity.ok(productService.getAllProducts(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/sizes")
    public ResponseEntity<List<ProductSize>> getProductSizes() {
        return ResponseEntity.ok(productService.getAllSizes());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Product> toggleProductAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleProductAvailability(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<java.util.Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Đã xóa món thành công"));
    }
}

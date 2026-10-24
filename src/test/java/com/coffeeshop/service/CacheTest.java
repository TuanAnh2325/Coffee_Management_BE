package com.coffeeshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CategoryService categoryService;

    @Test
    @DisplayName("Kiem tra Spring Cache da duoc bat va hoat dong voi CategoryService")
    void testSpringCacheEnabled() {
        assertNotNull(cacheManager, "CacheManager phai duoc khoi tao boi Spring Boot");

        Cache cache = cacheManager.getCache("categories");
        assertNotNull(cache, "Cache 'categories' phai ton tai");

        // Goi lan dau de dua vao cache
        categoryService.getAllCategories();
        assertNotNull(cache.get("all"), "Du lieu 'all' phai duoc luu trong cache 'categories'");

        // Xoa cache bang evict
        cache.clear();
        assertNull(cache.get("all"), "Sau khi clear cache, 'all' phai la null");
    }
}

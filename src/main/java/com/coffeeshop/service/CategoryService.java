package com.coffeeshop.service;

import com.coffeeshop.entity.Category;
import com.coffeeshop.exception.BadRequestException;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final com.coffeeshop.repository.ProductRepository productRepository;

    @Cacheable(value = "categories", key = "'all'")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Cacheable(value = "categories", key = "#id")
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(Category category) {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên danh mục không được để trống");
        }
        if (category.getCode() == null || category.getCode().trim().isEmpty()) {
            throw new BadRequestException("Mã danh mục không được để trống");
        }
        String code = category.getCode().trim().toUpperCase();
        if (categoryRepository.findByCode(code).isPresent()) {
            throw new BadRequestException("Mã danh mục '" + code + "' đã tồn tại");
        }
        category.setCode(code);
        category.setName(category.getName().trim());
        if (category.getIsActive() == null) {
            category.setIsActive(true);
        }
        return categoryRepository.save(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(Integer id, Category details) {
        Category category = getCategoryById(id);
        if (details.getName() != null && !details.getName().trim().isEmpty()) {
            category.setName(details.getName().trim());
        }
        if (details.getCode() != null && !details.getCode().trim().isEmpty()) {
            String newCode = details.getCode().trim().toUpperCase();
            if (!newCode.equalsIgnoreCase(category.getCode()) && categoryRepository.findByCode(newCode).isPresent()) {
                throw new BadRequestException("Mã danh mục '" + newCode + "' đã tồn tại");
            }
            category.setCode(newCode);
        }
        if (details.getDescription() != null) {
            category.setDescription(details.getDescription().trim());
        }
        if (details.getIsActive() != null) {
            category.setIsActive(details.getIsActive());
        }
        return categoryRepository.save(category);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        List<com.coffeeshop.entity.Product> products = productRepository.findByCategoryId(id);
        if (!products.isEmpty()) {
            throw new BadRequestException("Không thể xóa danh mục '" + category.getName() + "' vì đang có " + products.size() + " món thuộc danh mục này. Vui lòng chuyển hoặc xóa các món đó trước.");
        }
        categoryRepository.delete(category);
    }
}

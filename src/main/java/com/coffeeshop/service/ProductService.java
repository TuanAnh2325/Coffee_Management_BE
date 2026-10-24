package com.coffeeshop.service;

import com.coffeeshop.dto.ProductRequest;
import com.coffeeshop.entity.Category;
import com.coffeeshop.entity.InventoryItem;
import com.coffeeshop.entity.Product;
import com.coffeeshop.entity.ProductSize;
import com.coffeeshop.exception.BadRequestException;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.repository.CategoryRepository;
import com.coffeeshop.repository.InventoryItemRepository;
import com.coffeeshop.repository.ProductRepository;
import com.coffeeshop.repository.ProductSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final com.coffeeshop.repository.OrderItemRepository orderItemRepository;

    @Cacheable(value = "products", key = "#categoryId != null ? #categoryId : 'all'")
    public List<Product> getAllProducts(Integer categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
    }

    public List<ProductSize> getAllSizes() {
        return productSizeRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(ProductRequest request) {
        if (request == null) {
            throw new BadRequestException("Dữ liệu món không được để trống");
        }
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new BadRequestException("Mã món không được để trống");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên món không được để trống");
        }
        if (request.getCategoryId() == null) {
            throw new BadRequestException("Vui lòng chọn danh mục cho món");
        }
        if (request.getBasePrice() == null || request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Đơn giá cơ bản của món phải lớn hơn 0");
        }

        String code = request.getCode().trim().toUpperCase();
        if (productRepository.findByCode(code).isPresent()) {
            throw new BadRequestException("Mã món '" + code + "' đã tồn tại trong hệ thống");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        Product product = Product.builder()
                .code(code)
                .name(request.getName().trim())
                .category(category)
                .basePrice(request.getBasePrice())
                .imageUrl(request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty() 
                        ? request.getImageUrl().trim() 
                        : "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400")
                .description(request.getDescription())
                .isAvailable(true)
                .build();

        Product savedProduct = productRepository.save(product);

        // Theo DB docx quan he 1-1: Tao moi san pham tu dong khoi tao 1 ban ghi ton kho tuong ung
        int stock = (request.getInitialStock() != null && request.getInitialStock() >= 0) ? request.getInitialStock() : 50;
        InventoryItem inventoryItem = InventoryItem.builder()
                .product(savedProduct)
                .quantityInStock(stock)
                .minStockThreshold(10)
                .unit("ly")
                .build();
        inventoryItemRepository.save(inventoryItem);

        return savedProduct;
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);
        if (request == null) {
            throw new BadRequestException("Dữ liệu cập nhật không được để trống");
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            product.setName(request.getName().trim());
        }

        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            String newCode = request.getCode().trim().toUpperCase();
            if (!newCode.equalsIgnoreCase(product.getCode()) && productRepository.findByCode(newCode).isPresent()) {
                throw new BadRequestException("Mã món '" + newCode + "' đã tồn tại");
            }
            product.setCode(newCode);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if (request.getBasePrice() != null) {
            if (request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Đơn giá cơ bản phải lớn hơn 0");
            }
            product.setBasePrice(request.getBasePrice());
        }

        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            product.setImageUrl(request.getImageUrl().trim());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription().trim());
        }

        if (request.getInitialStock() != null) {
            inventoryItemRepository.findByProductId(id).ifPresent(inv -> {
                inv.setQuantityInStock(request.getInitialStock());
                inventoryItemRepository.save(inv);
            });
        }

        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product toggleProductAvailability(Long id) {
        Product product = getProductById(id);
        product.setIsAvailable(product.getIsAvailable() == null || !product.getIsAvailable());
        return productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        if (orderItemRepository.existsByProductId(id)) {
            throw new BadRequestException("Không thể xóa món '" + product.getName() + "' vì đã có trong lịch sử đơn hàng. Bạn có thể chọn ngưng phục vụ món thay vì xóa.");
        }
        inventoryItemRepository.findByProductId(id).ifPresent(inventoryItemRepository::delete);
        productRepository.delete(product);
    }
}

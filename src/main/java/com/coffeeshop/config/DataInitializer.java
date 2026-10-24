package com.coffeeshop.config;

import com.coffeeshop.entity.*;
import com.coffeeshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final DiningTableRepository diningTableRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role adminRole = roleRepository.save(Role.builder().name("ROLE_ADMIN").description("Quản trị viên").build());
            Role staffRole = roleRepository.save(Role.builder().name("ROLE_STAFF").description("Thu ngân").build());

            userRepository.save(User.builder()
                    .username("admin")
                    .passwordHash("$2a$10$7EqJtq98hPqEX7fNZaFWoO.8/q.e.F.kQY2WJ1a.P9kU4eZ7Kq.W")
                    .fullName("Quản trị viên")
                    .phone("0901234567")
                    .email("admin@aromacoffee.com")
                    .role(adminRole)
                    .isActive(true)
                    .build());

            userRepository.save(User.builder()
                    .username("cashier")
                    .passwordHash("$2a$10$7EqJtq98hPqEX7fNZaFWoO.8/q.e.F.kQY2WJ1a.P9kU4eZ7Kq.W")
                    .fullName("Nguyễn Văn Thu Ngân")
                    .phone("0912345678")
                    .email("cashier@aromacoffee.com")
                    .role(staffRole)
                    .isActive(true)
                    .build());
        }

        if (categoryRepository.count() == 0) {
            Category c1 = categoryRepository.save(Category.builder().code("COFFEE").name("Cà phê truyền thống & Pha máy").description("Cà phê đậm đà").isActive(true).build());
            Category c2 = categoryRepository.save(Category.builder().code("TEA").name("Trà trái cây nhiệt đới").description("Trà hoa quả thanh mát").isActive(true).build());
            Category c3 = categoryRepository.save(Category.builder().code("FREEZE").name("Đá xay & Sinh tố").description("Thức uống béo ngậy").isActive(true).build());
            Category c4 = categoryRepository.save(Category.builder().code("CAKE").name("Bánh ngọt & Điểm tâm").description("Bánh nướng tươi").isActive(true).build());

            productSizeRepository.save(ProductSize.builder().sizeName("S").extraPrice(BigDecimal.ZERO).description("350ml").build());
            productSizeRepository.save(ProductSize.builder().sizeName("M").extraPrice(new BigDecimal("5000")).description("500ml").build());
            productSizeRepository.save(ProductSize.builder().sizeName("L").extraPrice(new BigDecimal("10000")).description("700ml").build());

            Product p1 = productRepository.save(Product.builder().category(c1).code("CF-DEN").name("Cà phê Đen đá").basePrice(new BigDecimal("25000")).imageUrl("https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400").isAvailable(true).build());
            Product p2 = productRepository.save(Product.builder().category(c1).code("CF-SUA").name("Cà phê Sữa đá").basePrice(new BigDecimal("29000")).imageUrl("https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=400").isAvailable(true).build());
            Product p3 = productRepository.save(Product.builder().category(c1).code("BAC-XIU").name("Bạc xỉu 3 tầng").basePrice(new BigDecimal("32000")).imageUrl("https://images.unsplash.com/photo-1541167760496-1628856ab772?w=400").isAvailable(true).build());
            Product p4 = productRepository.save(Product.builder().category(c2).code("TRA-DAO").name("Trà Đào Cam Sả").basePrice(new BigDecimal("39000")).imageUrl("https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400").isAvailable(true).build());
            Product p5 = productRepository.save(Product.builder().category(c2).code("TRA-VAI").name("Trà Vải Hoa Lài").basePrice(new BigDecimal("39000")).imageUrl("https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400").isAvailable(true).build());
            Product p6 = productRepository.save(Product.builder().category(c3).code("FREEZE-MATCHA").name("Matcha Đá Xay Thạch").basePrice(new BigDecimal("45000")).imageUrl("https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=400").isAvailable(true).build());
            Product p7 = productRepository.save(Product.builder().category(c4).code("CAKE-TIRAMISU").name("Bánh Tiramisu Ý").basePrice(new BigDecimal("35000")).imageUrl("https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400").isAvailable(true).build());

            inventoryItemRepository.save(InventoryItem.builder().product(p1).quantityInStock(150).minStockThreshold(20).unit("Ly").build());
            inventoryItemRepository.save(InventoryItem.builder().product(p2).quantityInStock(120).minStockThreshold(20).unit("Ly").build());
            inventoryItemRepository.save(InventoryItem.builder().product(p3).quantityInStock(95).minStockThreshold(20).unit("Ly").build());
            inventoryItemRepository.save(InventoryItem.builder().product(p4).quantityInStock(80).minStockThreshold(15).unit("Ly").build());
            inventoryItemRepository.save(InventoryItem.builder().product(p5).quantityInStock(12).minStockThreshold(15).unit("Ly").build());
            inventoryItemRepository.save(InventoryItem.builder().product(p6).quantityInStock(45).minStockThreshold(10).unit("Ly").build());
            inventoryItemRepository.save(InventoryItem.builder().product(p7).quantityInStock(8).minStockThreshold(10).unit("Chiếc").build());
        }

        if (diningTableRepository.count() == 0) {
            for (int i = 1; i <= 4; i++) {
                diningTableRepository.save(DiningTable.builder().tableNumber("Bàn 0" + i).area("Tầng 1 (Trong nhà)").status("EMPTY").capacity(4).build());
            }
            diningTableRepository.save(DiningTable.builder().tableNumber("Bàn 05").area("Tầng 2 (Máy lạnh)").status("RESERVED").capacity(6).build());
            diningTableRepository.save(DiningTable.builder().tableNumber("Bàn 06").area("Tầng 2 (Máy lạnh)").status("EMPTY").capacity(4).build());
            diningTableRepository.save(DiningTable.builder().tableNumber("Bàn 07").area("Tầng 2 (Máy lạnh)").status("EMPTY").capacity(2).build());
            for (int i = 8; i <= 10; i++) {
                diningTableRepository.save(DiningTable.builder().tableNumber("Bàn " + (i < 10 ? "0" + i : i)).area("Sân vườn ngoài trời").status(i == 10 ? "SERVING" : "EMPTY").capacity(i == 10 ? 6 : 4).build());
            }
        }
    }
}

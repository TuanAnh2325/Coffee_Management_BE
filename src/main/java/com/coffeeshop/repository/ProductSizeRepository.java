package com.coffeeshop.repository;

import com.coffeeshop.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer> {
    Optional<ProductSize> findBySizeName(String sizeName);
}

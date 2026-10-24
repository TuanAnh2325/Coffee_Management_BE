package com.coffeeshop.repository;

import com.coffeeshop.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Integer> {
    Optional<DiningTable> findByTableNumber(String tableNumber);
    List<DiningTable> findByStatus(String status);
    List<DiningTable> findByArea(String area);
}

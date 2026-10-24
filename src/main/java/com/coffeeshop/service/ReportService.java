package com.coffeeshop.service;

import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.OrderItem;
import com.coffeeshop.repository.OrderItemRepository;
import com.coffeeshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public Map<String, Object> getTodayReport() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Order> todayOrders = orderRepository.findByCreatedAtBetween(startOfDay, endOfDay);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal cashRevenue = BigDecimal.ZERO;
        BigDecimal bankingRevenue = BigDecimal.ZERO;
        int completedCount = 0;
        int cancelledCount = 0;

        Map<String, Integer> productQuantities = new HashMap<>();
        Map<String, BigDecimal> productRevenues = new HashMap<>();

        for (Order order : todayOrders) {
            if ("COMPLETED".equalsIgnoreCase(order.getOrderStatus())) {
                completedCount++;
                totalRevenue = totalRevenue.add(order.getTotalAmount());
                if ("CASH".equalsIgnoreCase(order.getPaymentMethod())) {
                    cashRevenue = cashRevenue.add(order.getTotalAmount());
                } else {
                    bankingRevenue = bankingRevenue.add(order.getTotalAmount());
                }

                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
                        String name = item.getProductName();
                        productQuantities.put(name, productQuantities.getOrDefault(name, 0) + item.getQuantity());
                        productRevenues.put(name, productRevenues.getOrDefault(name, BigDecimal.ZERO).add(item.getSubtotal()));
                    }
                }
            } else if ("CANCELLED".equalsIgnoreCase(order.getOrderStatus())) {
                cancelledCount++;
            }
        }

        List<Map<String, Object>> topSelling = new ArrayList<>();
        productQuantities.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(e -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("productName", e.getKey());
                    map.put("quantitySold", e.getValue());
                    map.put("revenue", productRevenues.getOrDefault(e.getKey(), BigDecimal.ZERO));
                    topSelling.add(map);
                });

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("date", LocalDate.now().toString());
        report.put("totalRevenue", totalRevenue);
        report.put("totalOrders", todayOrders.size());
        report.put("completedOrders", completedCount);
        report.put("cancelledOrders", cancelledCount);
        report.put("cashRevenue", cashRevenue);
        report.put("bankingRevenue", bankingRevenue);
        report.put("topSellingProducts", topSelling);

        return report;
    }
}

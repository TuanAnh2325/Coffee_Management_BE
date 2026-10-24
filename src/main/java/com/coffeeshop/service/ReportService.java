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

        List<Order> todayOrders = orderRepository.findAll().stream()
                .filter(o -> {
                    LocalDateTime time = o.getCreatedAt() != null ? o.getCreatedAt() : o.getCompletedAt();
                    return time != null && !time.isBefore(startOfDay) && !time.isAfter(endOfDay);
                })
                .toList();

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

    public Map<String, Object> getMonthReport(Integer year, Integer month) {
        LocalDate now = LocalDate.now();
        int targetYear = (year != null && year >= 2000) ? year : now.getYear();
        int targetMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();

        LocalDate firstDay = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);
        LocalDateTime startOfMonth = firstDay.atStartOfDay();
        LocalDateTime endOfMonth = lastDay.atTime(LocalTime.MAX);

        List<Order> monthOrders = orderRepository.findAll().stream()
                .filter(o -> {
                    LocalDateTime time = o.getCreatedAt() != null ? o.getCreatedAt() : o.getCompletedAt();
                    return time != null && !time.isBefore(startOfMonth) && !time.isAfter(endOfMonth);
                })
                .toList();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal cashRevenue = BigDecimal.ZERO;
        BigDecimal bankingRevenue = BigDecimal.ZERO;
        int completedCount = 0;
        int cancelledCount = 0;

        Map<String, Integer> productQuantities = new HashMap<>();
        Map<String, BigDecimal> productRevenues = new HashMap<>();

        int daysInMonth = lastDay.getDayOfMonth();
        Map<Integer, BigDecimal> dailyRevenues = new HashMap<>();
        Map<Integer, Integer> dailyOrderCounts = new HashMap<>();
        for (int d = 1; d <= daysInMonth; d++) {
            dailyRevenues.put(d, BigDecimal.ZERO);
            dailyOrderCounts.put(d, 0);
        }

        for (Order order : monthOrders) {
            LocalDateTime orderTime = order.getCreatedAt() != null ? order.getCreatedAt() : order.getCompletedAt();
            int day = (orderTime != null) ? orderTime.getDayOfMonth() : 1;

            if ("COMPLETED".equalsIgnoreCase(order.getOrderStatus())) {
                completedCount++;
                totalRevenue = totalRevenue.add(order.getTotalAmount());
                if ("CASH".equalsIgnoreCase(order.getPaymentMethod())) {
                    cashRevenue = cashRevenue.add(order.getTotalAmount());
                } else {
                    bankingRevenue = bankingRevenue.add(order.getTotalAmount());
                }

                dailyRevenues.put(day, dailyRevenues.get(day).add(order.getTotalAmount()));
                dailyOrderCounts.put(day, dailyOrderCounts.get(day) + 1);

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
                .limit(10)
                .forEach(e -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("productName", e.getKey());
                    map.put("quantitySold", e.getValue());
                    map.put("revenue", productRevenues.getOrDefault(e.getKey(), BigDecimal.ZERO));
                    topSelling.add(map);
                });

        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (int d = 1; d <= daysInMonth; d++) {
            Map<String, Object> dayMap = new LinkedHashMap<>();
            dayMap.put("day", d);
            dayMap.put("date", String.format("%04d-%02d-%02d", targetYear, targetMonth, d));
            dayMap.put("revenue", dailyRevenues.get(d));
            dayMap.put("orderCount", dailyOrderCounts.get(d));
            dailyStats.add(dayMap);
        }

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("year", targetYear);
        report.put("month", targetMonth);
        report.put("monthLabel", String.format("Tháng %02d/%04d", targetMonth, targetYear));
        report.put("totalRevenue", totalRevenue);
        report.put("totalOrders", monthOrders.size());
        report.put("completedOrders", completedCount);
        report.put("cancelledOrders", cancelledCount);
        report.put("cashRevenue", cashRevenue);
        report.put("bankingRevenue", bankingRevenue);
        report.put("topSellingProducts", topSelling);
        report.put("dailyStats", dailyStats);

        return report;
    }
}

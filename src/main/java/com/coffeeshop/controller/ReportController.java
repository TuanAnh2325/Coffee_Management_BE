package com.coffeeshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("date", "2026-10-24");
        report.put("totalRevenue", new BigDecimal("12450000"));
        report.put("totalOrders", 142);
        report.put("completedOrders", 138);
        report.put("cancelledOrders", 4);
        report.put("cashRevenue", new BigDecimal("5200000"));
        report.put("bankingRevenue", new BigDecimal("7250000"));

        List<Map<String, Object>> topSelling = new ArrayList<>();
        Map<String, Object> m1 = new LinkedHashMap<>();
        m1.put("productName", "Cà phê Sữa đá");
        m1.put("quantitySold", 58);
        m1.put("revenue", new BigDecimal("1972000"));
        topSelling.add(m1);

        Map<String, Object> m2 = new LinkedHashMap<>();
        m2.put("productName", "Trà Đào Cam Sả");
        m2.put("quantitySold", 42);
        m2.put("revenue", new BigDecimal("1638000"));
        topSelling.add(m2);

        Map<String, Object> m3 = new LinkedHashMap<>();
        m3.put("productName", "Bạc xỉu 3 tầng");
        m3.put("quantitySold", 36);
        m3.put("revenue", new BigDecimal("1152000"));
        topSelling.add(m3);

        Map<String, Object> m4 = new LinkedHashMap<>();
        m4.put("productName", "Matcha Đá Xay");
        m4.put("quantitySold", 24);
        m4.put("revenue", new BigDecimal("1080000"));
        topSelling.add(m4);

        report.put("topSellingProducts", topSelling);

        return ResponseEntity.ok(report);
    }
}

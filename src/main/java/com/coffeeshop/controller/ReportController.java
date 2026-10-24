package com.coffeeshop.controller;

import com.coffeeshop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayReport() {
        return ResponseEntity.ok(reportService.getTodayReport());
    }
}

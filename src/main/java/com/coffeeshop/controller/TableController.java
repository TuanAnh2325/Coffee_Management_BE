package com.coffeeshop.controller;

import com.coffeeshop.entity.DiningTable;
import com.coffeeshop.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @GetMapping
    public ResponseEntity<List<DiningTable>> getAllTables(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String area) {
        return ResponseEntity.ok(tableService.getAllTables(status, area));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiningTable> getTableById(@PathVariable Integer id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DiningTable> updateTableStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        String newStatus = payload != null ? payload.get("status") : null;
        return ResponseEntity.ok(tableService.updateTableStatus(id, newStatus));
    }

    @PostMapping
    public ResponseEntity<DiningTable> createTable(@RequestBody DiningTable table) {
        return ResponseEntity.ok(tableService.createTable(table));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiningTable> updateTable(@PathVariable Integer id, @RequestBody DiningTable table) {
        return ResponseEntity.ok(tableService.updateTable(id, table));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTable(@PathVariable Integer id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bàn thành công"));
    }
}

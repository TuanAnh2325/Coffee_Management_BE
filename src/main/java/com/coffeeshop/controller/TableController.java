package com.coffeeshop.controller;

import com.coffeeshop.exception.BadRequestException;
import com.coffeeshop.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class TableController {

    private static final List<Map<String, Object>> MOCK_TABLES = new ArrayList<>();
    private static final Set<String> VALID_STATUSES = Set.of("EMPTY", "SERVING", "RESERVED");

    static {
        addTable(1, "Bàn 01", "Tầng 1 (Trong nhà)", "EMPTY", 4);
        addTable(2, "Bàn 02", "Tầng 1 (Trong nhà)", "SERVING", 4);
        addTable(3, "Bàn 03", "Tầng 1 (Trong nhà)", "SERVING", 2);
        addTable(4, "Bàn 04", "Tầng 1 (Trong nhà)", "EMPTY", 4);
        addTable(5, "Bàn 05", "Tầng 2 (Máy lạnh)", "RESERVED", 6);
        addTable(6, "Bàn 06", "Tầng 2 (Máy lạnh)", "EMPTY", 4);
        addTable(7, "Bàn 07", "Tầng 2 (Máy lạnh)", "SERVING", 2);
        addTable(8, "Bàn 08", "Sân vườn ngoài trời", "EMPTY", 4);
        addTable(9, "Bàn 09", "Sân vườn ngoài trời", "EMPTY", 4);
        addTable(10, "Bàn 10", "Sân vườn ngoài trời", "SERVING", 6);
    }

    private static void addTable(int id, String number, String area, String status, int capacity) {
        Map<String, Object> t = new LinkedHashMap<>();
        t.put("id", id);
        t.put("tableNumber", number);
        t.put("area", area);
        t.put("status", status);
        t.put("capacity", capacity);
        MOCK_TABLES.add(t);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllTables(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String area) {
        return ResponseEntity.ok(MOCK_TABLES);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTableById(@PathVariable Integer id) {
        return MOCK_TABLES.stream()
                .filter(t -> t.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateTableStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> payload) {
        String newStatus = payload != null ? payload.get("status") : null;
        if (newStatus == null || !VALID_STATUSES.contains(newStatus.toUpperCase())) {
            throw new BadRequestException("Trạng thái bàn không hợp lệ. Chỉ chấp nhận: EMPTY, SERVING, RESERVED");
        }

        for (Map<String, Object> t : MOCK_TABLES) {
            if (t.get("id").equals(id)) {
                t.put("status", newStatus.toUpperCase());
                return ResponseEntity.ok(t);
            }
        }
        throw new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id);
    }
}

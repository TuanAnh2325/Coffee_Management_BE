package com.coffeeshop.controller;

import com.coffeeshop.exception.BadRequestException;
import com.coffeeshop.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final List<Map<String, Object>> MOCK_ORDERS = new ArrayList<>();
    private static long orderSequence = 1001;

    static {
        Map<String, Object> o1 = new LinkedHashMap<>();
        o1.put("id", 1L);
        o1.put("orderCode", "HD20261024-0001");
        o1.put("tableName", "Bàn 02");
        o1.put("orderType", "DINE_IN");
        o1.put("orderStatus", "COMPLETED");
        o1.put("paymentStatus", "PAID");
        o1.put("paymentMethod", "CASH");
        o1.put("totalAmount", new BigDecimal("73000"));
        o1.put("cashierName", "Nguyễn Văn Thu Ngân");
        o1.put("createdAt", "2026-10-24 08:30:15");

        List<Map<String, Object>> items1 = new ArrayList<>();
        Map<String, Object> i1 = new LinkedHashMap<>();
        i1.put("productName", "Cà phê Sữa đá");
        i1.put("sizeName", "M");
        i1.put("unitPrice", new BigDecimal("34000"));
        i1.put("quantity", 1);
        i1.put("subtotal", new BigDecimal("34000"));
        i1.put("itemNote", "Ít ngọt");
        items1.add(i1);

        Map<String, Object> i2 = new LinkedHashMap<>();
        i2.put("productName", "Trà Đào Cam Sả");
        i2.put("sizeName", "S");
        i2.put("unitPrice", new BigDecimal("39000"));
        i2.put("quantity", 1);
        i2.put("subtotal", new BigDecimal("39000"));
        i2.put("itemNote", "Nhiều đào");
        items1.add(i2);

        o1.put("items", items1);
        MOCK_ORDERS.add(o1);

        Map<String, Object> o2 = new LinkedHashMap<>();
        o2.put("id", 2L);
        o2.put("orderCode", "HD20261024-0002");
        o2.put("tableName", "Mang về");
        o2.put("orderType", "TAKE_AWAY");
        o2.put("orderStatus", "COMPLETED");
        o2.put("paymentStatus", "PAID");
        o2.put("paymentMethod", "BANKING");
        o2.put("totalAmount", new BigDecimal("80000"));
        o2.put("cashierName", "Nguyễn Văn Thu Ngân");
        o2.put("createdAt", "2026-10-24 09:15:20");

        List<Map<String, Object>> items2 = new ArrayList<>();
        Map<String, Object> i3 = new LinkedHashMap<>();
        i3.put("productName", "Matcha Đá Xay Thạch");
        i3.put("sizeName", "L");
        i3.put("unitPrice", new BigDecimal("55000"));
        i3.put("quantity", 1);
        i3.put("subtotal", new BigDecimal("55000"));
        items2.add(i3);

        Map<String, Object> i4 = new LinkedHashMap<>();
        i4.put("productName", "Cà phê Đen đá");
        i4.put("sizeName", "S");
        i4.put("unitPrice", new BigDecimal("25000"));
        i4.put("quantity", 1);
        i4.put("subtotal", new BigDecimal("25000"));
        items2.add(i4);

        o2.put("items", items2);
        MOCK_ORDERS.add(o2);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        return ResponseEntity.ok(MOCK_ORDERS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        return MOCK_ORDERS.stream()
                .filter(o -> o.get("id").equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));
    }

    @PostMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> createMockOrder(@RequestBody Map<String, Object> payload) {
        if (payload == null) {
            throw new BadRequestException("Dữ liệu đơn hàng không được để trống");
        }

        String orderType = (String) payload.getOrDefault("orderType", "DINE_IN");
        Object tableId = payload.get("tableId");
        if ("DINE_IN".equalsIgnoreCase(orderType) && tableId == null) {
            throw new BadRequestException("Đơn hàng phục vụ tại quán bắt buộc phải chọn bàn (tableId không được để trống)");
        }

        Object itemsObj = payload.get("items");
        if (!(itemsObj instanceof List) || ((List<?>) itemsObj).isEmpty()) {
            throw new BadRequestException("Đơn hàng phải có ít nhất 1 món trong danh sách");
        }

        List<?> itemsList = (List<?>) itemsObj;
        for (Object itemObj : itemsList) {
            if (!(itemObj instanceof Map)) {
                throw new BadRequestException("Cấu trúc món trong đơn hàng không hợp lệ");
            }
            Map<?, ?> item = (Map<?, ?>) itemObj;
            Object quantityObj = item.get("quantity");
            if (quantityObj == null) {
                throw new BadRequestException("Số lượng món không được để trống");
            }

            int qty;
            try {
                qty = Integer.parseInt(quantityObj.toString());
            } catch (NumberFormatException e) {
                throw new BadRequestException("Số lượng món phải là một số nguyên");
            }

            if (qty <= 0) {
                throw new BadRequestException("Số lượng món phải lớn hơn 0 (không chấp nhận số âm hoặc bằng 0)");
            }
        }

        String paymentMethod = (String) payload.getOrDefault("paymentMethod", "CASH");
        if (!Set.of("CASH", "BANKING", "CARD").contains(paymentMethod.toUpperCase())) {
            throw new BadRequestException("Phương thức thanh toán không hợp lệ. Chỉ chấp nhận: CASH, BANKING, CARD");
        }

        long newId = orderSequence++;
        String orderCode = "HD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + newId;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", newId);
        response.put("orderCode", orderCode);
        response.put("orderType", orderType);
        response.put("tableId", tableId);
        response.put("orderStatus", "COMPLETED");
        response.put("paymentStatus", "PAID");
        response.put("paymentMethod", paymentMethod.toUpperCase());
        response.put("totalAmount", payload.getOrDefault("totalAmount", new BigDecimal("58000")));
        response.put("items", itemsList);
        response.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.put("message", "Đã tạo đơn hàng và in hóa đơn thành công");

        MOCK_ORDERS.add(0, response);
        return ResponseEntity.ok(response);
    }
}

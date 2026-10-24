package com.coffeeshop.service;

import com.coffeeshop.dto.OrderItemRequest;
import com.coffeeshop.dto.OrderRequest;
import com.coffeeshop.entity.*;
import com.coffeeshop.exception.BadRequestException;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final DiningTableRepository diningTableRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    private static final Set<String> VALID_PAYMENT_METHODS = Set.of("CASH", "BANKING", "CARD");

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));
    }

    @Transactional
    @CacheEvict(value = "tables", allEntries = true)
    public Order createOrder(OrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Dữ liệu đơn hàng không được để trống");
        }

        String orderType = request.getOrderType() != null ? request.getOrderType().toUpperCase() : "DINE_IN";
        DiningTable table = null;

        if ("DINE_IN".equals(orderType)) {
            if (request.getTableId() == null) {
                throw new BadRequestException("Đơn hàng phục vụ tại quán bắt buộc phải chọn bàn (tableId không được để trống)");
            }
            table = diningTableRepository.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + request.getTableId()));
            table.setStatus("SERVING");
            diningTableRepository.save(table);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Đơn hàng phải có ít nhất 1 món trong danh sách");
        }

        String paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod().toUpperCase() : "CASH";
        if (!VALID_PAYMENT_METHODS.contains(paymentMethod)) {
            throw new BadRequestException("Phương thức thanh toán không hợp lệ. Chỉ chấp nhận: CASH, BANKING, CARD");
        }

        User staff = userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản nhân viên thu ngân"));

        String orderCode = "HD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + (System.currentTimeMillis() % 10000);

        Order order = Order.builder()
                .orderCode(orderCode)
                .user(staff)
                .table(table)
                .orderType(orderType)
                .orderStatus("COMPLETED")
                .paymentStatus("PAID")
                .paymentMethod(paymentMethod)
                .totalAmount(BigDecimal.ZERO)
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        BigDecimal calculatedTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new BadRequestException("Số lượng món phải lớn hơn 0 (không chấp nhận số âm hoặc bằng 0)");
            }

            Product product = null;
            if (itemReq.getProductId() != null) {
                product = productRepository.findById(itemReq.getProductId()).orElse(null);
            }
            if (product == null && itemReq.getProductName() != null) {
                product = productRepository.findAll().stream()
                        .filter(p -> p.getName().equalsIgnoreCase(itemReq.getProductName()))
                        .findFirst()
                        .orElse(null);
            }
            if (product == null) {
                product = productRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin sản phẩm"));
            }

            Optional<InventoryItem> invOpt = inventoryItemRepository.findByProductId(product.getId());
            if (invOpt.isPresent()) {
                InventoryItem inv = invOpt.get();
                if (inv.getQuantityInStock() < itemReq.getQuantity()) {
                    throw new BadRequestException("Món '" + product.getName() + "' không đủ số lượng tồn kho (chỉ còn " + inv.getQuantityInStock() + " " + inv.getUnit() + ")");
                }
                inv.setQuantityInStock(inv.getQuantityInStock() - itemReq.getQuantity());
                inventoryItemRepository.save(inv);
            }

            String size = itemReq.getSizeName() != null ? itemReq.getSizeName().toUpperCase() : "M";
            BigDecimal extraPrice = BigDecimal.ZERO;
            if ("L".equals(size)) extraPrice = new BigDecimal("10000");
            else if ("M".equals(size)) extraPrice = new BigDecimal("5000");

            BigDecimal unitPrice = itemReq.getUnitPrice() != null ? itemReq.getUnitPrice() : product.getBasePrice().add(extraPrice);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            calculatedTotal = calculatedTotal.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .sizeName(size)
                    .unitPrice(unitPrice)
                    .quantity(itemReq.getQuantity())
                    .subtotal(subtotal)
                    .itemNote(itemReq.getItemNote())
                    .build();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(calculatedTotal);
        order.setItems(orderItems);

        return orderRepository.save(order);
    }
}

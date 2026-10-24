package com.coffeeshop.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String orderType;
    private Integer tableId;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private String note;
    private List<OrderItemRequest> items;
}

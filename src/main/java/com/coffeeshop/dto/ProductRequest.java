package com.coffeeshop.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String code;
    private String name;
    private Integer categoryId;
    private BigDecimal basePrice;
    private String imageUrl;
    private String description;
    private Integer initialStock;
}

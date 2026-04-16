package com.tfgkk.farmer_platform.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class OrderItem {
    private Long productId;
    private String name;
    private String sku;
    private Integer quantity;
    private Integer price;
}

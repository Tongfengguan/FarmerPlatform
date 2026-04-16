package com.tfgkk.farmer_platform.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ProductSku {

    private String name;
    private Integer price;
    private Integer stock;
}

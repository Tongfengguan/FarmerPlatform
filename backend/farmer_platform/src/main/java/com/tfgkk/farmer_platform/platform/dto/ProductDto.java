package com.tfgkk.farmer_platform.platform.dto;

import java.util.List;

public record ProductDto(
        Long id,
        String name,
        String categoryL1,
        String categoryL2,
        String image,
        String detail,
        String freightType,
        int price,
        Integer oldPrice,
        int stock,
        int salesCount,
        String status,
        List<ProductSkuDto> skus) {
}

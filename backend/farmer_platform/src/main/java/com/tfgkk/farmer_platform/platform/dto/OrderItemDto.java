package com.tfgkk.farmer_platform.platform.dto;

public record OrderItemDto(Long productId, String name, String sku, int quantity, int price) {
}

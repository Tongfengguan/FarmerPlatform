package com.tfgkk.farmer_platform.dto.platform;

public record OrderItemDto(Long productId, String name, String sku, int quantity, int price) {
}

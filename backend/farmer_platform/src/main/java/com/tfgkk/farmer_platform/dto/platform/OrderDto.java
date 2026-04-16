package com.tfgkk.farmer_platform.dto.platform;

import java.util.List;

public record OrderDto(
        String id,
        Long userId,
        String buyer,
        String phone,
        List<OrderItemDto> items,
        int payAmount,
        int freightAmount,
        String status,
        String createdAt,
        String shipCompany,
        String shipNo,
        AddressDto receiver) {
}

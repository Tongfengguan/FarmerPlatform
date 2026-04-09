package com.tfgkk.farmer_platform.platform.dto;

public record UserSummaryDto(
        Long id,
        String name,
        String phone,
        String status,
        String createdAt,
        int orders,
        int spend,
        String lastActive) {
}

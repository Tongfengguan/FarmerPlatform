package com.tfgkk.farmer_platform.dto.auth;

public record AuthResponse(
        Long id,
        String account,
        String nickname,
        String phone,
        String role,
        String token) {
}

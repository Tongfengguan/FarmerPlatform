package com.tfgkk.farmer_platform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Nickname is required")
    private String nickname;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    private String password;
}

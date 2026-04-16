package com.tfgkk.farmer_platform.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Account is required")
    @Size(min = 2, max = 50, message = "Account length must be between 2 and 50")
    private String account;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^1\\d{10}$", message = "Phone format is invalid")
    private String phone;

    @Size(min = 6, max = 32, message = "Password length must be between 6 and 32")
    private String password;

    @Size(max = 50, message = "Nickname length must be less than 50")
    private String nickname;
}

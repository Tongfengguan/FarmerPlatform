package com.tfgkk.farmer_platform.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(message = "Account is required")
    private String account;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^1\\d{10}$", message = "Phone format is invalid")
    private String phone;

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 32, message = "Password length must be between 6 and 32")
    private String nextPassword;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNextPassword() {
        return nextPassword;
    }

    public void setNextPassword(String nextPassword) {
        this.nextPassword = nextPassword;
    }
}

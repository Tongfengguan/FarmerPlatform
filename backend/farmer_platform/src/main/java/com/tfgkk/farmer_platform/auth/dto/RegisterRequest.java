package com.tfgkk.farmer_platform.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

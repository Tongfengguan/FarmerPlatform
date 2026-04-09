package com.tfgkk.farmer_platform.auth;

import com.tfgkk.farmer_platform.auth.dto.AuthResponse;
import com.tfgkk.farmer_platform.auth.dto.LoginRequest;
import com.tfgkk.farmer_platform.auth.dto.RegisterRequest;
import com.tfgkk.farmer_platform.auth.dto.ResetPasswordRequest;
import com.tfgkk.farmer_platform.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful", authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("Register successful", authService.register(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset successful");
    }

    @GetMapping("/me")
    public ApiResponse<AuthResponse> me(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.CURRENT_USER_ID);
        return ApiResponse.success("Current user fetched", authService.getCurrentUser(userId));
    }
}

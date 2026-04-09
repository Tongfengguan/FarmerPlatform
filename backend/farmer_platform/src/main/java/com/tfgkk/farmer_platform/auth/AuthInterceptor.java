package com.tfgkk.farmer_platform.auth;

import com.tfgkk.farmer_platform.common.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER_ID = "currentUserId";

    private final JwtService jwtService;

    public AuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException("Authentication token is required");
        }

        String token = authorization.substring(7);
        Long userId = jwtService.parseUserId(token);
        request.setAttribute(CURRENT_USER_ID, userId);
        return true;
    }
}

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
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取请求路径
        String path = request.getRequestURI();
        // 如果是登录、注册、重置密码或者获取产品列表等公开接口，直接放行
        if (path.contains("/api/auth/login") || 
            path.contains("/api/auth/register") || 
            path.contains("/api/auth/reset-password") ||
            path.contains("/api/platform/products") ||
            path.contains("/api/platform/bootstrap") ||
            path.contains("/api/platform/articles")) {
            return true;
        }

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

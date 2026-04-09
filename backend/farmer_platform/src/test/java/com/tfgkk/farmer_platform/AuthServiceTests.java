package com.tfgkk.farmer_platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tfgkk.farmer_platform.auth.AuthService;
import com.tfgkk.farmer_platform.auth.dto.LoginRequest;
import com.tfgkk.farmer_platform.auth.dto.RegisterRequest;
import com.tfgkk.farmer_platform.auth.dto.ResetPasswordRequest;
import com.tfgkk.farmer_platform.common.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Test
    void shouldLoginWithSeedUser() {
        LoginRequest request = new LoginRequest();
        request.setAccount("张大农");
        request.setPassword("123456");

        assertEquals("user", authService.login(request).role());
        org.junit.jupiter.api.Assertions.assertFalse(authService.login(request).token().isBlank());
    }

    @Test
    void shouldLoginWithSeedAdmin() {
        LoginRequest request = new LoginRequest();
        request.setAccount("tfgkk");
        request.setPassword("123456");

        assertEquals("admin", authService.login(request).role());
    }

    @Test
    void shouldRegisterAndLoginNewUser() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAccount("newfarmer");
        registerRequest.setPhone("13812345678");
        registerRequest.setPassword("123456");
        registerRequest.setNickname("新农户");

        assertEquals("user", authService.register(registerRequest).role());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("newfarmer");
        loginRequest.setPassword("123456");

        assertEquals("新农户", authService.login(loginRequest).nickname());
    }

    @Test
    void shouldRejectDuplicateAccount() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAccount("张大农");
        registerRequest.setPhone("13899990000");
        registerRequest.setPassword("123456");

        assertThrows(BusinessException.class, () -> authService.register(registerRequest));
    }

    @Test
    void shouldResetPasswordAndLoginWithNewPassword() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setAccount("张大农");
        request.setPhone("13800008821");
        request.setNextPassword("654321");

        authService.resetPassword(request);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("张大农");
        loginRequest.setPassword("654321");

        assertEquals("user", authService.login(loginRequest).role());
    }
}

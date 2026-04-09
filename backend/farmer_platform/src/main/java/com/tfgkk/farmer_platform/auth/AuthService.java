package com.tfgkk.farmer_platform.auth;

import com.tfgkk.farmer_platform.auth.dto.AuthResponse;
import com.tfgkk.farmer_platform.auth.dto.LoginRequest;
import com.tfgkk.farmer_platform.auth.dto.RegisterRequest;
import com.tfgkk.farmer_platform.auth.dto.ResetPasswordRequest;
import com.tfgkk.farmer_platform.common.BusinessException;
import com.tfgkk.farmer_platform.user.UserAccount;
import com.tfgkk.farmer_platform.user.UserAccountRepository;
import com.tfgkk.farmer_platform.user.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        UserAccount userAccount = userAccountRepository.findByAccount(normalize(request.getAccount()))
                .orElseThrow(() -> new BusinessException("Account or password is incorrect"));

        if (!Boolean.TRUE.equals(userAccount.getEnabled())) {
            throw new BusinessException("This account has been disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), userAccount.getPasswordHash())) {
            throw new BusinessException("Account or password is incorrect");
        }

        return toResponse(userAccount);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String account = normalize(request.getAccount());
        String phone = normalize(request.getPhone());

        if (userAccountRepository.existsByAccount(account)) {
            throw new BusinessException("Account already exists");
        }

        if (userAccountRepository.existsByPhone(phone)) {
            throw new BusinessException("Phone already exists");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setAccount(account);
        userAccount.setPhone(phone);
        userAccount.setNickname(normalizeNickname(request.getNickname(), account));
        userAccount.setRole(UserRole.USER);
        userAccount.setEnabled(Boolean.TRUE);
        userAccount.setPasswordHash(passwordEncoder.encode(normalizePassword(request.getPassword())));

        UserAccount saved = userAccountRepository.save(userAccount);
        return toResponse(saved);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        UserAccount userAccount = userAccountRepository.findByAccount(normalize(request.getAccount()))
                .orElseThrow(() -> new BusinessException("Account and phone do not match"));

        if (!normalize(userAccount.getPhone()).equals(normalize(request.getPhone()))) {
            throw new BusinessException("Account and phone do not match");
        }

        userAccount.setPasswordHash(passwordEncoder.encode(normalizePassword(request.getNextPassword())));
        userAccountRepository.save(userAccount);
    }

    @Transactional(readOnly = true)
    public AuthResponse getCurrentUser(Long userId) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User does not exist"));
        return toResponse(userAccount, null);
    }

    private AuthResponse toResponse(UserAccount userAccount) {
        return toResponse(userAccount, jwtService.generateToken(userAccount));
    }

    private AuthResponse toResponse(UserAccount userAccount, String token) {
        return new AuthResponse(
                userAccount.getId(),
                userAccount.getAccount(),
                userAccount.getNickname(),
                userAccount.getPhone(),
                userAccount.getRole().name().toLowerCase(),
                token);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizePassword(String password) {
        String normalized = normalize(password);
        return normalized.isBlank() ? "123456" : normalized;
    }

    private String normalizeNickname(String nickname, String account) {
        String normalized = normalize(nickname);
        return normalized.isBlank() ? account : normalized;
    }
}

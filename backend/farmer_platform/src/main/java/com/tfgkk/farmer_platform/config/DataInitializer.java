package com.tfgkk.farmer_platform.config;

import com.tfgkk.farmer_platform.entity.UserAccount;
import com.tfgkk.farmer_platform.repository.UserAccountRepository;
import com.tfgkk.farmer_platform.entity.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAccount("张大农", "13800008821", "123456", "张大农", UserRole.USER);
        seedAccount("tfgkk", "13900000000", "123456", "平台管理员", UserRole.ADMIN);
    }

    private void seedAccount(String account, String phone, String password, String nickname, UserRole role) {
        if (userAccountRepository.existsByAccount(account)) {
            return;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setAccount(account);
        userAccount.setPhone(phone);
        userAccount.setPasswordHash(passwordEncoder.encode(password));
        userAccount.setNickname(nickname);
        userAccount.setRole(role);
        userAccount.setEnabled(Boolean.TRUE);
        userAccountRepository.save(userAccount);
    }
}

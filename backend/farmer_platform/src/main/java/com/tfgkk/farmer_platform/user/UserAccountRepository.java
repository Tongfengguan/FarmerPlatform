package com.tfgkk.farmer_platform.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByPhone(String phone);
}

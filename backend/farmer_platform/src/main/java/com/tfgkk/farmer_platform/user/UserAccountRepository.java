package com.tfgkk.farmer_platform.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByPhone(String phone);

    @Query(value = "SELECT u.id, u.account, u.phone, " +
            "CASE WHEN u.enabled = true THEN '正常' ELSE '禁用' END as status, " +
            "CAST(u.created_at AS DATE) as createdAt, " +
            "COUNT(o.id) as orderCount, " +
            "COALESCE(SUM(CASE WHEN o.status <> '已取消' THEN o.pay_amount ELSE 0 END), 0) as totalSpend, " +
            "COALESCE(CAST(MAX(o.created_at) AS DATE), CAST(u.created_at AS DATE)) as lastActive " +
            "FROM user_accounts u " +
            "LEFT JOIN orders o ON u.id = o.user_id " +
            "GROUP BY u.id, u.account, u.phone, u.enabled, u.created_at " +
            "ORDER BY u.id ASC", nativeQuery = true)
    List<Object[]> findUserSummaries();
}

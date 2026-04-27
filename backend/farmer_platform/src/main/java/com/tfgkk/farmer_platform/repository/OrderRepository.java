package com.tfgkk.farmer_platform.repository;

import com.tfgkk.farmer_platform.entity.OrderEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<OrderEntity> findAllByOrderByCreatedAtDesc();

    long countByStatusIn(List<String> statuses);

    @Query("SELECT SUM(o.payAmount) FROM OrderEntity o WHERE o.status <> '已取消'")
    Integer calculateMonthlySales();
}

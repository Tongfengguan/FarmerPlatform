package com.tfgkk.farmer_platform.platform;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}

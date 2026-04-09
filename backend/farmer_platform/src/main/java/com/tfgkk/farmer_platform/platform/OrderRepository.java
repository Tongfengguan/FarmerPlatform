package com.tfgkk.farmer_platform.platform;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}

package com.tfgkk.farmer_platform.repository;

import com.tfgkk.farmer_platform.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}

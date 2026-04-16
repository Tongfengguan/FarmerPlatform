package com.tfgkk.farmer_platform.repository;

import com.tfgkk.farmer_platform.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
}

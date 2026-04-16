package com.tfgkk.farmer_platform.dto.platform;

public record ArticleDto(
        Long id,
        String title,
        String category,
        String cover,
        String summary,
        String content,
        String source,
        boolean isPush,
        String status,
        int viewCount,
        String publishedAt) {
}

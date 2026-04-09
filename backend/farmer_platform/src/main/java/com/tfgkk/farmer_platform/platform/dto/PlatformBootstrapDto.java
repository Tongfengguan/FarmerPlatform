package com.tfgkk.farmer_platform.platform.dto;

import java.util.List;

public record PlatformBootstrapDto(
        DashboardDto dashboard,
        List<ArticleDto> articles,
        List<ProductDto> products) {
}

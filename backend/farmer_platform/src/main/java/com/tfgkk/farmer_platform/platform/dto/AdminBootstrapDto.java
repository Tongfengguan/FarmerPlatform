package com.tfgkk.farmer_platform.platform.dto;

import java.util.List;

public record AdminBootstrapDto(
        DashboardDto dashboard,
        List<ArticleDto> articles,
        List<ProductDto> products,
        List<UserSummaryDto> users,
        List<OrderDto> orders) {
}

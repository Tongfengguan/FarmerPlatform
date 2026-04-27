package com.tfgkk.farmer_platform.service;

import com.tfgkk.farmer_platform.common.BusinessException;
import com.tfgkk.farmer_platform.dto.platform.*;
import com.tfgkk.farmer_platform.entity.*;
import com.tfgkk.farmer_platform.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlatformService {

    private final ArticleRepository articleRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserAccountRepository userAccountRepository;
    
    private final ArticleService articleService;
    private final ProductService productService;
    private final OrderService orderService;

    public PlatformService(
            ArticleRepository articleRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            UserAccountRepository userAccountRepository,
            ArticleService articleService,
            ProductService productService,
            OrderService orderService) {
        this.articleRepository = articleRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userAccountRepository = userAccountRepository;
        this.articleService = articleService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @Transactional(readOnly = true)
    public PlatformBootstrapDto bootstrapPublic() {
        Pageable articlePage = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Pageable productPage = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
        return new PlatformBootstrapDto(
                buildDashboard(),
                articleRepository.findAll(articlePage).stream().map(articleService::toArticleDto).toList(),
                productRepository.findAll(productPage).stream().map(productService::toProductDto).toList());
    }

    @Transactional(readOnly = true)
    public AdminBootstrapDto bootstrapAdmin() {
        Pageable recentPage = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Pageable orderPage = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return new AdminBootstrapDto(
                buildDashboard(),
                articleRepository.findAll(recentPage).stream().map(articleService::toArticleDto).toList(),
                productRepository.findAll(recentPage).stream().map(productService::toProductDto).toList(),
                listUsers(),
                orderRepository.findAll(orderPage).stream().map(orderService::toOrderDto).toList());
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> listUsers() {
        return userAccountRepository.findUserSummaries().stream().map(row -> new UserSummaryDto(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                row[4].toString(),
                ((Number) row[5]).intValue(),
                ((Number) row[6]).intValue(),
                row[7].toString()
        )).toList();
    }

    @Transactional
    public UserSummaryDto toggleUserStatus(Long userId) {
        UserAccount user = requireUser(userId);
        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        return listUsers().stream().filter(u -> u.id().equals(userId)).findFirst().orElse(null);
    }

    public void assertAdmin(Long userId) {
        UserAccount user = requireUser(userId);
        if (user.getRole() != UserRole.ADMIN) throw new BusinessException("Admin permission required");
    }

    private DashboardDto buildDashboard() {
        long pendingOrders = orderRepository.countByStatusIn(List.of("待付款", "待发货"));
        
        Integer salesSum = orderRepository.calculateMonthlySales();
        int salesMonth = salesSum != null ? salesSum : 0;
        
        long publishedArticles = articleRepository.countByStatus("已发布");
        
        // 模拟一个动态的访问量（实际项目中应由 Redis 或埋点表统计）
        int visitToday = 1200 + (int)(orderRepository.count() * 5); 
        
        return new DashboardDto(visitToday, salesMonth, (int)pendingOrders, (int)publishedArticles);
    }

    private UserAccount requireUser(Long userId) {
        return userAccountRepository.findById(userId).orElseThrow(() -> new BusinessException("User not found"));
    }
}

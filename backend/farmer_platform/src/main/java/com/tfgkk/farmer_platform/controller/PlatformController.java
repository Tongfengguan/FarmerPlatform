package com.tfgkk.farmer_platform.controller;

import com.tfgkk.farmer_platform.security.AuthInterceptor;
import com.tfgkk.farmer_platform.common.ApiResponse;
import com.tfgkk.farmer_platform.common.PagedResponse;
import com.tfgkk.farmer_platform.dto.auth.AuthResponse;
import com.tfgkk.farmer_platform.dto.auth.UpdateProfileRequest;
import com.tfgkk.farmer_platform.dto.platform.*;
import com.tfgkk.farmer_platform.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlatformController {

    private final PlatformService platformService;
    private final ArticleService articleService;
    private final ProductService productService;
    private final OrderService orderService;
    private final AuthService authService;

    public PlatformController(PlatformService platformService, ArticleService articleService, 
                              ProductService productService, OrderService orderService,
                              AuthService authService) {
        this.platformService = platformService;
        this.articleService = articleService;
        this.productService = productService;
        this.orderService = orderService;
        this.authService = authService;
    }

    @GetMapping("/api/platform/bootstrap")
    public ApiResponse<PlatformBootstrapDto> bootstrap() {
        return ApiResponse.success("Bootstrap fetched", platformService.bootstrapPublic());
    }

    @GetMapping("/api/platform/addresses")
    public ApiResponse<List<AddressDto>> addresses(HttpServletRequest request) {
        return ApiResponse.success("Addresses fetched", orderService.listAddresses(currentUserId(request)));
    }

    @PostMapping("/api/platform/addresses")
    public ApiResponse<AddressDto> addAddress(HttpServletRequest request, @Valid @RequestBody CreateAddressRequest body) {
        return ApiResponse.success("Address saved", orderService.addAddress(currentUserId(request), body));
    }

    @GetMapping("/api/platform/articles")
    public ApiResponse<PagedResponse<ArticleDto>> articles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Articles fetched", PagedResponse.fromPage(articleService.listArticles(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))));
    }

    @GetMapping("/api/platform/products")
    public ApiResponse<PagedResponse<ProductDto>> products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success("Products fetched", PagedResponse.fromPage(productService.listProducts(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))));
    }

    @GetMapping("/api/platform/orders")
    public ApiResponse<PagedResponse<OrderDto>> orders(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success("Orders fetched", PagedResponse.fromPage(orderService.listMyOrders(currentUserId(request), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))));
    }

    @PostMapping("/api/platform/orders")
    public ApiResponse<OrderDto> createOrder(HttpServletRequest request, @Valid @RequestBody CreateOrderRequest body) {
        return ApiResponse.success("Order created", orderService.createOrder(currentUserId(request), body));
    }

    @PutMapping("/api/platform/profile")
    public ApiResponse<AuthResponse> updateProfile(HttpServletRequest request, @Valid @RequestBody UpdateProfileRequest body) {
        return ApiResponse.success("Profile updated", authService.updateProfile(currentUserId(request), body));
    }

    @PatchMapping("/api/platform/orders/{orderId}/pay")
    public ApiResponse<OrderDto> payOrder(HttpServletRequest request, @PathVariable String orderId) {
        return ApiResponse.success("Order paid", orderService.payOrder(currentUserId(request), orderId));
    }

    @PatchMapping("/api/platform/orders/{orderId}/cancel")
    public ApiResponse<OrderDto> cancelOrder(HttpServletRequest request, @PathVariable String orderId) {
        return ApiResponse.success("Order cancelled", orderService.cancelOrder(currentUserId(request), orderId));
    }

    @PatchMapping("/api/platform/orders/{orderId}/confirm")
    public ApiResponse<OrderDto> confirmOrder(HttpServletRequest request, @PathVariable String orderId) {
        return ApiResponse.success("Order confirmed", orderService.confirmOrder(currentUserId(request), orderId));
    }

    @PatchMapping("/api/platform/articles/{articleId}/view")
    public ApiResponse<ArticleDto> viewArticle(@PathVariable Long articleId) {
        return ApiResponse.success("Article viewed", articleService.incrementArticleView(articleId));
    }

    @GetMapping("/api/admin/bootstrap")
    public ApiResponse<AdminBootstrapDto> adminBootstrap(HttpServletRequest request) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Admin bootstrap fetched", platformService.bootstrapAdmin());
    }

    @PostMapping("/api/admin/articles")
    public ApiResponse<ArticleDto> createArticle(HttpServletRequest request, @RequestBody ArticleDto body) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Article saved", articleService.saveArticle(body));
    }

    @PutMapping("/api/admin/articles/{articleId}")
    public ApiResponse<ArticleDto> updateArticle(HttpServletRequest request, @PathVariable Long articleId, @RequestBody ArticleDto body) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Article updated", articleService.saveArticle(new ArticleDto(articleId, body.title(), body.category(), body.cover(), body.summary(), body.content(), body.source(), body.isPush(), body.status(), body.viewCount(), body.publishedAt())));
    }

    @DeleteMapping("/api/admin/articles/{articleId}")
    public ApiResponse<Void> deleteArticle(HttpServletRequest request, @PathVariable Long articleId) {
        platformService.assertAdmin(currentUserId(request));
        articleService.deleteArticle(articleId);
        return ApiResponse.success("Article deleted", null);
    }

    @PatchMapping("/api/admin/articles/{articleId}/toggle-status")
    public ApiResponse<ArticleDto> toggleArticle(HttpServletRequest request, @PathVariable Long articleId) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Article status toggled", articleService.toggleArticleStatus(articleId));
    }

    @PostMapping("/api/admin/products")
    public ApiResponse<ProductDto> createProduct(HttpServletRequest request, @RequestBody ProductDto body) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Product saved", productService.saveProduct(body));
    }

    @PutMapping("/api/admin/products/{productId}")
    public ApiResponse<ProductDto> updateProduct(HttpServletRequest request, @PathVariable Long productId, @RequestBody ProductDto body) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Product updated", productService.saveProduct(new ProductDto(productId, body.name(), body.categoryL1(), body.categoryL2(), body.image(), body.detail(), body.freightType(), body.price(), body.oldPrice(), body.stock(), body.salesCount(), body.status(), body.skus())));
    }

    @PatchMapping("/api/admin/products/{productId}/toggle-status")
    public ApiResponse<ProductDto> toggleProduct(HttpServletRequest request, @PathVariable Long productId) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Product status toggled", productService.toggleProductStatus(productId));
    }

    @GetMapping("/api/admin/users")
    public ApiResponse<List<UserSummaryDto>> users(HttpServletRequest request) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Users fetched", platformService.listUsers());
    }

    @PatchMapping("/api/admin/users/{userId}/toggle-status")
    public ApiResponse<UserSummaryDto> toggleUser(HttpServletRequest request, @PathVariable Long userId) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("User status toggled", platformService.toggleUserStatus(userId));
    }

    @GetMapping("/api/admin/orders")
    public ApiResponse<PagedResponse<OrderDto>> adminOrders(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Orders fetched", PagedResponse.fromPage(orderService.listAllOrders(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))));
    }

    @PatchMapping("/api/admin/orders/{orderId}/ship")
    public ApiResponse<OrderDto> shipOrder(HttpServletRequest request, @PathVariable String orderId, @Valid @RequestBody ShipOrderRequest body) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Order shipped", orderService.shipOrder(orderId, body));
    }

    @PatchMapping("/api/admin/orders/{orderId}/refund")
    public ApiResponse<OrderDto> refundOrder(HttpServletRequest request, @PathVariable String orderId) {
        platformService.assertAdmin(currentUserId(request));
        return ApiResponse.success("Order refunded", orderService.refundOrder(orderId));
    }

    private Long currentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(AuthInterceptor.CURRENT_USER_ID);
    }
}

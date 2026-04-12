package com.tfgkk.farmer_platform.platform;

import com.tfgkk.farmer_platform.common.BusinessException;
import com.tfgkk.farmer_platform.platform.dto.AddressDto;
import com.tfgkk.farmer_platform.platform.dto.AdminBootstrapDto;
import com.tfgkk.farmer_platform.platform.dto.ArticleDto;
import com.tfgkk.farmer_platform.platform.dto.CreateAddressRequest;
import com.tfgkk.farmer_platform.platform.dto.CreateOrderRequest;
import com.tfgkk.farmer_platform.platform.dto.DashboardDto;
import com.tfgkk.farmer_platform.platform.dto.OrderDto;
import com.tfgkk.farmer_platform.platform.dto.OrderItemDto;
import com.tfgkk.farmer_platform.platform.dto.PlatformBootstrapDto;
import com.tfgkk.farmer_platform.platform.dto.ProductDto;
import com.tfgkk.farmer_platform.platform.dto.ProductSkuDto;
import com.tfgkk.farmer_platform.platform.dto.ShipOrderRequest;
import com.tfgkk.farmer_platform.platform.dto.UserSummaryDto;
import com.tfgkk.farmer_platform.user.UserAccount;
import com.tfgkk.farmer_platform.user.UserAccountRepository;
import com.tfgkk.farmer_platform.user.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformService {

    private final ArticleRepository articleRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final UserAccountRepository userAccountRepository;

    public PlatformService(
            ArticleRepository articleRepository,
            ProductRepository productRepository,
            AddressRepository addressRepository,
            OrderRepository orderRepository,
            UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public PlatformBootstrapDto bootstrapPublic() {
        return new PlatformBootstrapDto(
                buildDashboard(),
                articleRepository.findAll().stream().map(this::toArticleDto).sorted(Comparator.comparing(ArticleDto::id).reversed()).toList(),
                productRepository.findAll().stream().map(this::toProductDto).sorted(Comparator.comparing(ProductDto::id).reversed()).toList());
    }

    @Transactional(readOnly = true)
    public AdminBootstrapDto bootstrapAdmin() {
        return new AdminBootstrapDto(
                buildDashboard(),
                articleRepository.findAll().stream().map(this::toArticleDto).sorted(Comparator.comparing(ArticleDto::id).reversed()).toList(),
                productRepository.findAll().stream().map(this::toProductDto).sorted(Comparator.comparing(ProductDto::id).reversed()).toList(),
                listUsers(),
                orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toOrderDto).toList());
    }

    @Transactional(readOnly = true)
    public List<AddressDto> listAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIdAsc(userId).stream().map(this::toAddressDto).toList();
    }

    @Transactional
    public List<AddressDto> addAddress(Long userId, CreateAddressRequest request) {
        UserAccount user = requireUser(userId);
        if (request.isDefault()) {
            addressRepository.findByUserOrderByIdAsc(user).forEach(address -> address.setIsDefault(false));
        }
        AddressEntity entity = new AddressEntity();
        entity.setUser(user);
        entity.setName(request.getName().trim());
        entity.setPhone(request.getPhone().trim());
        entity.setAddress(request.getAddress().trim());
        entity.setIsDefault(request.isDefault() || addressRepository.findByUserOrderByIdAsc(user).isEmpty());
        addressRepository.save(entity);
        return listAddresses(userId);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> listMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toOrderDto).toList();
    }

    @Transactional
    public List<OrderDto> createOrder(Long userId, CreateOrderRequest request) {
        UserAccount user = requireUser(userId);
        AddressEntity receiver = addressRepository.findByUserOrderByIdAsc(user).stream()
                .filter(AddressEntity::getIsDefault)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Please add a default address first"));

        for (OrderItemDto item : request.getItems()) {
            ProductEntity product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new BusinessException("Product not found: " + item.name()));
            
            if (product.getStock() < item.quantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - item.quantity());
            
            if (item.sku() != null && !item.sku().isEmpty() && !product.getSkus().isEmpty()) {
                ProductSku targetSku = product.getSkus().stream()
                        .filter(s -> s.getName().equals(item.sku()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException("SKU not found: " + item.sku()));
                
                if (targetSku.getStock() < item.quantity()) {
                    throw new BusinessException("Insufficient stock for SKU: " + item.sku());
                }
                targetSku.setStock(targetSku.getStock() - item.quantity());
            }
            
            productRepository.save(product);
        }

        int payAmount = request.getItems().stream().mapToInt(item -> item.price() * item.quantity()).sum();
        int freightAmount = payAmount >= 99 ? 0 : 12;
        OrderEntity order = new OrderEntity();
        order.setId(generateOrderId());
        order.setUser(user);
        order.setBuyer(user.getAccount());
        order.setPhone(user.getPhone());
        order.setItems(request.getItems().stream().map(this::toOrderItem).toList());
        order.setPayAmount(payAmount);
        order.setFreightAmount(freightAmount);
        order.setStatus("待付款");
        order.setCreatedAt(LocalDateTime.now());
        order.setShipCompany("");
        order.setShipNo("");
        order.setReceiverName(receiver.getName());
        order.setReceiverPhone(receiver.getPhone());
        order.setReceiverAddress(receiver.getAddress());
        orderRepository.save(order);
        return listMyOrders(userId);
    }

    @Transactional
    public List<OrderDto> payOrder(Long userId, String orderId) {
        OrderEntity order = requireOwnedOrder(userId, orderId);
        if ("待付款".equals(order.getStatus())) order.setStatus("待发货");
        return listMyOrders(userId);
    }

    @Transactional
    public List<OrderDto> cancelOrder(Long userId, String orderId) {
        OrderEntity order = requireOwnedOrder(userId, orderId);
        if ("待付款".equals(order.getStatus())) order.setStatus("已取消");
        return listMyOrders(userId);
    }

    @Transactional
    public List<OrderDto> confirmOrder(Long userId, String orderId) {
        OrderEntity order = requireOwnedOrder(userId, orderId);
        if ("待收货".equals(order.getStatus())) order.setStatus("已完成");
        return listMyOrders(userId);
    }

    @Transactional
    public List<OrderDto> shipOrder(String orderId, ShipOrderRequest request) {
        OrderEntity order = requireOrder(orderId);
        order.setShipCompany(request.getShipCompany().trim());
        order.setShipNo(request.getShipNo().trim());
        order.setStatus("待收货");
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toOrderDto).toList();
    }

    @Transactional
    public List<OrderDto> refundOrder(String orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setStatus("已取消");
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toOrderDto).toList();
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> listUsers() {
        List<OrderEntity> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return userAccountRepository.findAll().stream().map(user -> {
            List<OrderEntity> userOrders = orders.stream().filter(order -> order.getUser().getId().equals(user.getId())).toList();
            int spend = userOrders.stream().filter(order -> !"已取消".equals(order.getStatus())).mapToInt(OrderEntity::getPayAmount).sum();
            String lastActive = userOrders.isEmpty() ? LocalDate.now().toString() : userOrders.get(0).getCreatedAt().toLocalDate().toString();
            return new UserSummaryDto(user.getId(), user.getAccount(), user.getPhone(), Boolean.TRUE.equals(user.getEnabled()) ? "正常" : "禁用", user.getCreatedAt().toLocalDate().toString(), userOrders.size(), spend, lastActive);
        }).sorted(Comparator.comparing(UserSummaryDto::id)).toList();
    }

    @Transactional
    public List<UserSummaryDto> toggleUserStatus(Long userId) {
        UserAccount user = requireUser(userId);
        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        return listUsers();
    }

    @Transactional
    public List<ArticleDto> saveArticle(ArticleDto articleDto) {
        ArticleEntity entity = articleDto.id() == null ? new ArticleEntity() : requireArticle(articleDto.id());
        entity.setTitle(articleDto.title());
        entity.setCategory(articleDto.category());
        entity.setCover(articleDto.cover());
        entity.setSummary(articleDto.summary());
        entity.setContent(articleDto.content());
        entity.setSource(articleDto.source());
        entity.setIsPush(articleDto.isPush());
        entity.setStatus(articleDto.status());
        entity.setPublishedAt(articleDto.publishedAt() == null ? LocalDate.now() : LocalDate.parse(articleDto.publishedAt()));
        if (entity.getViewCount() == null) entity.setViewCount(0);
        articleRepository.save(entity);
        return articleRepository.findAll().stream().map(this::toArticleDto).sorted(Comparator.comparing(ArticleDto::id).reversed()).toList();
    }

    @Transactional
    public List<ArticleDto> toggleArticleStatus(Long articleId) {
        ArticleEntity article = requireArticle(articleId);
        article.setStatus("已发布".equals(article.getStatus()) ? "已下架" : "已发布");
        return articleRepository.findAll().stream().map(this::toArticleDto).sorted(Comparator.comparing(ArticleDto::id).reversed()).toList();
    }

    @Transactional
    public List<ArticleDto> deleteArticle(Long articleId) {
        articleRepository.delete(requireArticle(articleId));
        return articleRepository.findAll().stream().map(this::toArticleDto).sorted(Comparator.comparing(ArticleDto::id).reversed()).toList();
    }

    @Transactional
    public List<ArticleDto> incrementArticleView(Long articleId) {
        ArticleEntity article = requireArticle(articleId);
        article.setViewCount(article.getViewCount() + 1);
        return articleRepository.findAll().stream().map(this::toArticleDto).sorted(Comparator.comparing(ArticleDto::id).reversed()).toList();
    }

    @Transactional
    public List<ProductDto> saveProduct(ProductDto productDto) {
        ProductEntity entity = productDto.id() == null ? new ProductEntity() : requireProduct(productDto.id());
        entity.setName(productDto.name());
        entity.setCategoryL1(productDto.categoryL1());
        entity.setCategoryL2(productDto.categoryL2());
        entity.setImage(productDto.image());
        entity.setDetail(productDto.detail());
        entity.setFreightType(productDto.freightType());
        entity.setPrice(productDto.price());
        entity.setOldPrice(productDto.oldPrice());
        entity.setStock(productDto.stock());
        entity.setSalesCount(productDto.salesCount());
        entity.setStatus(productDto.status());
        entity.setSkus(productDto.skus().stream().map(this::toProductSku).collect(Collectors.toList()));
        productRepository.save(entity);
        return productRepository.findAll().stream().map(this::toProductDto).sorted(Comparator.comparing(ProductDto::id).reversed()).toList();
    }

    @Transactional
    public List<ProductDto> toggleProductStatus(Long productId) {
        ProductEntity product = requireProduct(productId);
        product.setStatus("销售中".equals(product.getStatus()) ? "已下架" : "销售中");
        return productRepository.findAll().stream().map(this::toProductDto).sorted(Comparator.comparing(ProductDto::id).reversed()).toList();
    }

    public void assertAdmin(Long userId) {
        UserAccount user = requireUser(userId);
        if (user.getRole() != UserRole.ADMIN) throw new BusinessException("Admin permission required");
    }

    private DashboardDto buildDashboard() {
        int pendingOrders = (int) orderRepository.findAllByOrderByCreatedAtDesc().stream().filter(order -> "待付款".equals(order.getStatus()) || "待发货".equals(order.getStatus())).count();
        int salesMonth = orderRepository.findAllByOrderByCreatedAtDesc().stream().filter(order -> !"已取消".equals(order.getStatus())).mapToInt(OrderEntity::getPayAmount).sum();
        int publishedArticles = (int) articleRepository.findAll().stream().filter(article -> "已发布".equals(article.getStatus())).count();
        return new DashboardDto(2841, salesMonth, pendingOrders, publishedArticles);
    }

    private UserAccount requireUser(Long userId) { return userAccountRepository.findById(userId).orElseThrow(() -> new BusinessException("User not found")); }
    private ArticleEntity requireArticle(Long articleId) { return articleRepository.findById(articleId).orElseThrow(() -> new BusinessException("Article not found")); }
    private ProductEntity requireProduct(Long productId) { return productRepository.findById(productId).orElseThrow(() -> new BusinessException("Product not found")); }
    private OrderEntity requireOrder(String orderId) { return orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Order not found")); }

    private OrderEntity requireOwnedOrder(Long userId, String orderId) {
        OrderEntity order = requireOrder(orderId);
        if (!order.getUser().getId().equals(userId)) throw new BusinessException("No permission to operate this order");
        return order;
    }

    private ArticleDto toArticleDto(ArticleEntity article) {
        return new ArticleDto(article.getId(), article.getTitle(), article.getCategory(), article.getCover(), article.getSummary(), article.getContent(), article.getSource(), Boolean.TRUE.equals(article.getIsPush()), article.getStatus(), article.getViewCount(), article.getPublishedAt().toString());
    }

    private ProductDto toProductDto(ProductEntity product) {
        return new ProductDto(product.getId(), product.getName(), product.getCategoryL1(), product.getCategoryL2(), product.getImage(), product.getDetail(), product.getFreightType(), product.getPrice(), product.getOldPrice(), product.getStock(), product.getSalesCount(), product.getStatus(), product.getSkus().stream().map(sku -> new ProductSkuDto(sku.getName(), sku.getPrice(), sku.getStock())).toList());
    }

    private AddressDto toAddressDto(AddressEntity address) {
        return new AddressDto(address.getId(), address.getName(), address.getPhone(), address.getAddress(), Boolean.TRUE.equals(address.getIsDefault()));
    }

    private OrderDto toOrderDto(OrderEntity order) {
        return new OrderDto(order.getId(), order.getUser().getId(), order.getBuyer(), order.getPhone(), order.getItems().stream().map(item -> new OrderItemDto(item.getProductId(), item.getName(), item.getSku(), item.getQuantity(), item.getPrice())).toList(), order.getPayAmount(), order.getFreightAmount(), order.getStatus(), order.getCreatedAt().toString().replace('T', ' '), order.getShipCompany(), order.getShipNo(), new AddressDto(null, order.getReceiverName(), order.getReceiverPhone(), order.getReceiverAddress(), true));
    }

    private OrderItem toOrderItem(OrderItemDto item) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(item.productId());
        orderItem.setName(item.name());
        orderItem.setSku(item.sku());
        orderItem.setQuantity(item.quantity());
        orderItem.setPrice(item.price());
        return orderItem;
    }

    private ProductSku toProductSku(ProductSkuDto skuDto) {
        ProductSku sku = new ProductSku();
        sku.setName(skuDto.name());
        sku.setPrice(skuDto.price());
        sku.setStock(skuDto.stock());
        return sku;
    }

    private String generateOrderId() { return String.valueOf(System.currentTimeMillis()); }
}

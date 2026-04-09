package com.tfgkk.farmer_platform.config;

import com.tfgkk.farmer_platform.platform.AddressEntity;
import com.tfgkk.farmer_platform.platform.AddressRepository;
import com.tfgkk.farmer_platform.platform.ArticleEntity;
import com.tfgkk.farmer_platform.platform.ArticleRepository;
import com.tfgkk.farmer_platform.platform.OrderEntity;
import com.tfgkk.farmer_platform.platform.OrderItem;
import com.tfgkk.farmer_platform.platform.OrderRepository;
import com.tfgkk.farmer_platform.platform.ProductEntity;
import com.tfgkk.farmer_platform.platform.ProductRepository;
import com.tfgkk.farmer_platform.platform.ProductSku;
import com.tfgkk.farmer_platform.user.UserAccount;
import com.tfgkk.farmer_platform.user.UserAccountRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PlatformDataInitializer implements CommandLineRunner {

    private final ArticleRepository articleRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final UserAccountRepository userAccountRepository;

    public PlatformDataInitializer(
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

    @Override
    public void run(String... args) {
        seedArticles();
        seedProducts();
        seedUserData();
    }

    private void seedArticles() {
        if (articleRepository.count() > 0) return;
        articleRepository.saveAll(List.of(
                article("2025粮食直补政策解读：申领流程一文看懂", "政策法规", "围绕粮食直补、良种补贴和农机补贴梳理申领路径。", "为稳定粮食生产，多地持续提高补贴标准，本文整理了申报材料和时间节点。", "农业农村部门", true, "已发布", 3421, LocalDate.of(2025, 3, 15), "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=1200&q=80"),
                article("春耕生产技术指导：小麦返青期管理要点", "农技科普", "从追肥、灌溉和病虫害防治三个角度总结返青期管理。", "返青期是形成有效穗的重要阶段，建议结合墒情分次施肥。", "省农科院", false, "已发布", 2108, LocalDate.of(2025, 3, 14), "https://images.unsplash.com/photo-1464226184884-fa280b87c399?auto=format&fit=crop&w=1200&q=80"),
                article("3月蔬菜批发价格行情报告", "市场动态", "重点监测叶菜、瓜果和根茎类产品价格走势。", "叶菜价格受天气影响波动较大，建议合理安排采收节奏。", "农业信息中心", false, "已发布", 1876, LocalDate.of(2025, 3, 13), "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1200&q=80"),
                article("农机购置补贴申请流程详解", "补贴资讯", "梳理农机购置补贴申请流程、材料要求和注意事项。", "申请农机补贴需要准备购机发票、身份证明和备案信息。", "农业农村局", true, "已发布", 4512, LocalDate.of(2025, 3, 12), "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=1200&q=80")));
    }

    private void seedProducts() {
        if (productRepository.count() > 0) return;
        productRepository.saveAll(List.of(
                product("优质水稻种子（5kg）", "农业生产资料", "种子种苗", "精选高产抗病品种，适合南方双季稻区。", "包邮", 89, 120, 342, 156, "销售中", "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=1200&q=80", List.of(sku("5kg", 89, 210), sku("10kg", 168, 132))),
                product("有机复合肥（25kg）", "农业生产资料", "有机肥料", "通用型复合肥，适用于蔬菜、果树、粮食作物。", "统一运费", 185, 210, 280, 89, "销售中", "https://images.unsplash.com/photo-1615811361523-6bd03d7748e7?auto=format&fit=crop&w=1200&q=80", List.of(sku("25kg", 185, 180), sku("50kg", 350, 100))),
                product("新鲜有机西红柿（5斤装）", "农产品", "新鲜果蔬", "基地现摘直发，口感沙甜。", "包邮", 38, null, 120, 312, "销售中", "https://images.unsplash.com/photo-1546470427-e5ac89cd0b6d?auto=format&fit=crop&w=1200&q=80", List.of(sku("5斤装", 38, 120))),
                product("小型电动喷雾器", "农机设备", "植保设备", "轻量化机身，适合果园和温室作业。", "按地区", 680, 880, 45, 23, "销售中", "https://images.unsplash.com/photo-1592982537447-7440770cbfc9?auto=format&fit=crop&w=1200&q=80", List.of(sku("标准版", 680, 30), sku("长续航版", 799, 15))),
                product("农用无人机（基础版）", "农机设备", "植保设备", "适合中小规模植保作业，支持自动航线。", "按地区", 12800, null, 8, 5, "销售中", "https://images.unsplash.com/photo-1473448912268-2022ce9509d8?auto=format&fit=crop&w=1200&q=80", List.of(sku("基础版", 12800, 8)))));
    }

    private void seedUserData() {
        UserAccount user = userAccountRepository.findByAccount("张大农").orElse(null);
        if (user == null) return;
        if (addressRepository.findByUserOrderByIdAsc(user).isEmpty()) {
            addressRepository.save(address(user, "张大农", "13800008821", "江苏省南京市玄武区农业路12号", true));
            addressRepository.save(address(user, "张大农", "13800008821", "江苏省南京市栖霞区稻香路88号", false));
        }
        if (orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).isEmpty()) {
            orderRepository.save(order(user, "20250315001", 178, 0, "待发货", LocalDateTime.of(2025, 3, 15, 9, 22), "", "", "张大农", "13800008821", "江苏省南京市玄武区农业路12号", List.of(orderItem(1L, "优质水稻种子（5kg）", "5kg", 2, 89))));
            orderRepository.save(order(user, "20250312019", 114, 0, "待付款", LocalDateTime.of(2025, 3, 12, 16, 47), "", "", "张大农", "13800008821", "江苏省南京市玄武区农业路12号", List.of(orderItem(3L, "新鲜有机西红柿（5斤装）", "5斤装", 3, 38))));
        }
    }

    private ArticleEntity article(String title, String category, String summary, String content, String source, boolean isPush, String status, int viewCount, LocalDate publishedAt, String cover) {
        ArticleEntity entity = new ArticleEntity();
        entity.setTitle(title);
        entity.setCategory(category);
        entity.setSummary(summary);
        entity.setContent(content);
        entity.setSource(source);
        entity.setIsPush(isPush);
        entity.setStatus(status);
        entity.setViewCount(viewCount);
        entity.setPublishedAt(publishedAt);
        entity.setCover(cover);
        return entity;
    }

    private ProductEntity product(String name, String categoryL1, String categoryL2, String detail, String freightType, int price, Integer oldPrice, int stock, int salesCount, String status, String image, List<ProductSku> skus) {
        ProductEntity entity = new ProductEntity();
        entity.setName(name);
        entity.setCategoryL1(categoryL1);
        entity.setCategoryL2(categoryL2);
        entity.setDetail(detail);
        entity.setFreightType(freightType);
        entity.setPrice(price);
        entity.setOldPrice(oldPrice);
        entity.setStock(stock);
        entity.setSalesCount(salesCount);
        entity.setStatus(status);
        entity.setImage(image);
        entity.setSkus(skus);
        return entity;
    }

    private ProductSku sku(String name, int price, int stock) {
        ProductSku sku = new ProductSku();
        sku.setName(name);
        sku.setPrice(price);
        sku.setStock(stock);
        return sku;
    }

    private AddressEntity address(UserAccount user, String name, String phone, String address, boolean isDefault) {
        AddressEntity entity = new AddressEntity();
        entity.setUser(user);
        entity.setName(name);
        entity.setPhone(phone);
        entity.setAddress(address);
        entity.setIsDefault(isDefault);
        return entity;
    }

    private OrderEntity order(UserAccount user, String id, int payAmount, int freightAmount, String status, LocalDateTime createdAt, String shipCompany, String shipNo, String receiverName, String receiverPhone, String receiverAddress, List<OrderItem> items) {
        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        entity.setUser(user);
        entity.setBuyer(user.getAccount());
        entity.setPhone(user.getPhone());
        entity.setPayAmount(payAmount);
        entity.setFreightAmount(freightAmount);
        entity.setStatus(status);
        entity.setCreatedAt(createdAt);
        entity.setShipCompany(shipCompany);
        entity.setShipNo(shipNo);
        entity.setReceiverName(receiverName);
        entity.setReceiverPhone(receiverPhone);
        entity.setReceiverAddress(receiverAddress);
        entity.setItems(items);
        return entity;
    }

    private OrderItem orderItem(Long productId, String name, String sku, int quantity, int price) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setName(name);
        item.setSku(sku);
        item.setQuantity(quantity);
        item.setPrice(price);
        return item;
    }
}

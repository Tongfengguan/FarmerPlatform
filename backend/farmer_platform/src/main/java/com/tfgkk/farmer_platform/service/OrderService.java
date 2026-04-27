package com.tfgkk.farmer_platform.service;

import com.tfgkk.farmer_platform.common.BusinessException;
import com.tfgkk.farmer_platform.dto.platform.*;
import com.tfgkk.farmer_platform.entity.*;
import com.tfgkk.farmer_platform.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class OrderService {

    private static final Random RANDOM = new Random();

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final UserAccountRepository userAccountRepository;

    public OrderService(
            OrderRepository orderRepository,
            AddressRepository addressRepository,
            ProductRepository productRepository,
            UserAccountRepository userAccountRepository) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> listMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(this::toOrderDto);
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> listAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toOrderDto);
    }

    @Transactional(readOnly = true)
    public List<AddressDto> listAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIdAsc(userId).stream().map(this::toAddressDto).toList();
    }

    @Transactional
    public AddressDto addAddress(Long userId, CreateAddressRequest request) {
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
        return toAddressDto(addressRepository.save(entity));
    }

    @Transactional
    public OrderDto createOrder(Long userId, CreateOrderRequest request) {
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
        return toOrderDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto payOrder(Long userId, String orderId) {
        OrderEntity order = requireOwnedOrder(userId, orderId);
        if ("待付款".equals(order.getStatus())) order.setStatus("待发货");
        return toOrderDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(Long userId, String orderId) {
        OrderEntity order = requireOwnedOrder(userId, orderId);
        if ("待付款".equals(order.getStatus())) order.setStatus("已取消");
        return toOrderDto(order);
    }

    @Transactional
    public OrderDto confirmOrder(Long userId, String orderId) {
        OrderEntity order = requireOwnedOrder(userId, orderId);
        if ("待收货".equals(order.getStatus())) order.setStatus("已完成");
        return toOrderDto(order);
    }

    @Transactional
    public OrderDto shipOrder(String orderId, ShipOrderRequest request) {
        OrderEntity order = requireOrder(orderId);
        order.setShipCompany(request.getShipCompany().trim());
        order.setShipNo(request.getShipNo().trim());
        order.setStatus("待收货");
        return toOrderDto(order);
    }

    @Transactional
    public OrderDto refundOrder(String orderId) {
        OrderEntity order = requireOrder(orderId);
        order.setStatus("已取消");
        return toOrderDto(order);
    }

    public OrderEntity requireOrder(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Order not found"));
    }

    private OrderEntity requireOwnedOrder(Long userId, String orderId) {
        OrderEntity order = requireOrder(orderId);
        if (!order.getUser().getId().equals(userId)) throw new BusinessException("No permission to operate this order");
        return order;
    }

    private UserAccount requireUser(Long userId) {
        return userAccountRepository.findById(userId).orElseThrow(() -> new BusinessException("User not found"));
    }

    public OrderDto toOrderDto(OrderEntity order) {
        return new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getBuyer(),
                order.getPhone(),
                order.getItems().stream().map(item -> new OrderItemDto(item.getProductId(), item.getName(), item.getSku(), item.getQuantity(), item.getPrice())).toList(),
                order.getPayAmount(),
                order.getFreightAmount(),
                order.getStatus(),
                order.getCreatedAt().toString().replace('T', ' '),
                order.getShipCompany(),
                order.getShipNo(),
                new AddressDto(null, order.getReceiverName(), order.getReceiverPhone(), order.getReceiverAddress(), true)
        );
    }

    private AddressDto toAddressDto(AddressEntity address) {
        return new AddressDto(address.getId(), address.getName(), address.getPhone(), address.getAddress(), Boolean.TRUE.equals(address.getIsDefault()));
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

    private String generateOrderId() {
        return LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%04d", RANDOM.nextInt(10000));
    }
}

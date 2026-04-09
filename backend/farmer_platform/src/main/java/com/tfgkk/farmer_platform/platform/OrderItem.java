package com.tfgkk.farmer_platform.platform;

import jakarta.persistence.Embeddable;

@Embeddable
public class OrderItem {
    private Long productId;
    private String name;
    private String sku;
    private Integer quantity;
    private Integer price;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
}

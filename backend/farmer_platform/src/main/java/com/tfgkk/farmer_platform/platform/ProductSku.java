package com.tfgkk.farmer_platform.platform;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProductSku {

    private String name;
    private Integer price;
    private Integer stock;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}

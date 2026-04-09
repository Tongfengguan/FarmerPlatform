package com.tfgkk.farmer_platform.platform;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 50)
    private String categoryL1;

    @Column(nullable = false, length = 50)
    private String categoryL2;

    @Column(nullable = false, length = 500)
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false, length = 50)
    private String freightType;

    @Column(nullable = false)
    private Integer price;

    private Integer oldPrice;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Integer salesCount;

    @Column(nullable = false, length = 50)
    private String status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_sku", joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductSku> skus = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (salesCount == null) salesCount = 0;
        if (stock == null) stock = 0;
    }

    @PreUpdate
    public void preUpdate() { updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategoryL1() { return categoryL1; }
    public void setCategoryL1(String categoryL1) { this.categoryL1 = categoryL1; }
    public String getCategoryL2() { return categoryL2; }
    public void setCategoryL2(String categoryL2) { this.categoryL2 = categoryL2; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getFreightType() { return freightType; }
    public void setFreightType(String freightType) { this.freightType = freightType; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public Integer getOldPrice() { return oldPrice; }
    public void setOldPrice(Integer oldPrice) { this.oldPrice = oldPrice; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getSalesCount() { return salesCount; }
    public void setSalesCount(Integer salesCount) { this.salesCount = salesCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<ProductSku> getSkus() { return skus; }
    public void setSkus(List<ProductSku> skus) { this.skus = skus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

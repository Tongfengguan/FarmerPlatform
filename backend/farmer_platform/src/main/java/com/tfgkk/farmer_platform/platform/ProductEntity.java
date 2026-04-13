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
import lombok.Data;

@Data
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
}

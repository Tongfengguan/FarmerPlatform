package com.tfgkk.farmer_platform.service;

import com.tfgkk.farmer_platform.common.BusinessException;
import com.tfgkk.farmer_platform.dto.platform.ProductDto;
import com.tfgkk.farmer_platform.dto.platform.ProductSkuDto;
import com.tfgkk.farmer_platform.entity.ProductEntity;
import com.tfgkk.farmer_platform.entity.ProductSku;
import com.tfgkk.farmer_platform.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> listProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toProductDto);
    }

    @Transactional
    public ProductDto saveProduct(ProductDto productDto) {
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
        return toProductDto(productRepository.save(entity));
    }

    @Transactional
    public ProductDto toggleProductStatus(Long productId) {
        ProductEntity product = requireProduct(productId);
        product.setStatus("销售中".equals(product.getStatus()) ? "已下架" : "销售中");
        return toProductDto(product);
    }

    public ProductEntity requireProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new BusinessException("Product not found"));
    }

    public ProductDto toProductDto(ProductEntity product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getCategoryL1(),
                product.getCategoryL2(),
                product.getImage(),
                product.getDetail(),
                product.getFreightType(),
                product.getPrice(),
                product.getOldPrice(),
                product.getStock(),
                product.getSalesCount(),
                product.getStatus(),
                product.getSkus().stream().map(sku -> new ProductSkuDto(sku.getName(), sku.getPrice(), sku.getStock())).toList()
        );
    }

    private ProductSku toProductSku(ProductSkuDto skuDto) {
        ProductSku sku = new ProductSku();
        sku.setName(skuDto.name());
        sku.setPrice(skuDto.price());
        sku.setStock(skuDto.stock());
        return sku;
    }
}

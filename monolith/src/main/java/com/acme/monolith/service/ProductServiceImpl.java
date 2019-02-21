package com.acme.monolith.service;

import com.acme.monolith.domain.InventoryItem;
import com.acme.monolith.mapper.ProductMapper;
import com.acme.monolith.repository.InventoryItemRepository;
import com.acme.monolith.repository.ProductRepository;
import com.acme.monolith.resource.dto.ProductDetail;
import com.acme.monolith.resource.dto.ProductSummaryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final InventoryItemRepository inventoryRepository;
  private final ProductMapper mapper;

  public ProductServiceImpl(ProductRepository productRepository, InventoryItemRepository inventoryRepository, ProductMapper mapper) {
    this.productRepository = productRepository;
    this.inventoryRepository = inventoryRepository;
    this.mapper = mapper;
  }

  @Override
  public List<ProductSummaryResponse> getAllProductSummaries() {
    return productRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
  }

  @Override
  public Optional<ProductDetail> getProductDetails(String productCode) {
    return productRepository.findByCode(productCode).flatMap(product -> {
      return inventoryRepository.findByProductCode(product.getCode()).map(inventoryItem -> {
        ProductDetail productDetail = mapper.toProductDetailPartial(product);
        productDetail.setQuantity(inventoryItem.getQuantity());
        return productDetail;
      });
    });
  }
}

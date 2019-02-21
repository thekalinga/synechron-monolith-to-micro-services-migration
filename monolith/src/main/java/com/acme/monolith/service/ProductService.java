package com.acme.monolith.service;

import com.acme.monolith.resource.dto.ProductDetail;
import com.acme.monolith.resource.dto.ProductSummaryResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {
  List<ProductSummaryResponse> getAllProductSummaries();
  Optional<ProductDetail> getProductDetails(String productCode);
}

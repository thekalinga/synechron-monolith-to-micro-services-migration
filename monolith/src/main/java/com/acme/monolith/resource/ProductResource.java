package com.acme.monolith.resource;

import com.acme.monolith.resource.dto.ProductDetail;
import com.acme.monolith.resource.dto.ProductSummaryResponse;
import com.acme.monolith.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductResource {

  private final ProductService service;

  public ProductResource(ProductService service) {
    this.service = service;
  }

  @GetMapping
  List<ProductSummaryResponse> listProductSummaries() {
    return service.getAllProductSummaries();
  }

  @GetMapping("/{productCode}")
  ResponseEntity<ProductDetail> getProductDetails(@PathVariable String productCode) {
    return service.getProductDetails(productCode)
        .map(productDetail -> ResponseEntity.ok().body(productDetail))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

}

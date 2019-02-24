package com.acme.micro.inventory.resource;

import com.acme.common.order.inventory.ExpiringInventoryLeaseRestRequest;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.micro.inventory.resource.dto.IdContainer;
import com.acme.micro.inventory.resource.dto.InventoryLeaseCancellationResponse;
import com.acme.micro.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping
public class InventoryResource {
  private final InventoryService service;

  public InventoryResource(InventoryService service) {
    this.service = service;
  }

  //  @GetMapping
//  List<ProductSummaryResponse> listProductSummaries() {
//    return service.getAllProductSummaries();
//  }
//
//  @GetMapping("/{productCode}")
//  ResponseEntity<ProductDetail> getProductDetails(@PathVariable String productCode) {
//    return service.getProductDetails(productCode)
//        .map(productDetail -> ResponseEntity.ok().body(productDetail))
//        .orElseGet(() -> ResponseEntity.notFound().build());
//  }

  @PostMapping("/acquireExpiringLease")
  ExpiringInventoryLeaseRestResponse attemptExpiringLeaseAcquire(@RequestBody @Valid ExpiringInventoryLeaseRestRequest request) {
    return service.acquireLease(request);
  }

  @PostMapping("/cancelLease")
  InventoryLeaseCancellationResponse cancelLease(@RequestBody @Valid IdContainer id) {
    // TODO: Fix this
    return InventoryLeaseCancellationResponse.builder().cancelled(true).build();
  }
}

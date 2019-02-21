package com.acme.micro.inventory.resource;

import com.acme.micro.inventory.resource.dto.ExpiringInventoryLeaseRequest;
import com.acme.micro.inventory.resource.dto.ExpiringInventoryLeaseResponse;
import com.acme.micro.inventory.resource.dto.IdContainer;
import com.acme.micro.inventory.resource.dto.InventoryAcquireConfirmationResponse;
import com.acme.micro.inventory.resource.dto.InventoryLeaseCancellationResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping
public class InventoryResource {

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
  ExpiringInventoryLeaseResponse attemptExpiringLeaseAcquire(@RequestBody @Valid ExpiringInventoryLeaseRequest request) {
    // TODO: Fix this
    return ExpiringInventoryLeaseResponse.builder().leaseId(1).leaseAcquired(true).leaseValidTill(
        LocalDateTime.now().plusMinutes(1)).productName("Dummy").build();
  }

  @PostMapping("/confirmAcquire")
  InventoryAcquireConfirmationResponse confirmAcquire(@RequestBody @Valid IdContainer id) {
    // TODO: Fix this
    return InventoryAcquireConfirmationResponse.builder().acquired(true).build();
  }

  @PostMapping("/cancelLease")
  InventoryLeaseCancellationResponse cancelLease(@RequestBody @Valid IdContainer id) {
    // TODO: Fix this
    return InventoryLeaseCancellationResponse.builder().cancelled(true).build();
  }
}

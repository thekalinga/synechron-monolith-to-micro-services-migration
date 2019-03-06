package com.acme.micro.inventory.resource;

import com.acme.common.order.inventory.ExpiringInventoryLeaseRestRequest;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.micro.inventory.resource.contract.InventoryItemResponse;
import com.acme.micro.inventory.resource.contract.InventoryLeaseCancellationResponse;
import com.acme.micro.inventory.service.InventoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping
public class InventoryResource {
  private final InventoryService service;

  public InventoryResource(InventoryService service) {
    this.service = service;
  }

  @PreAuthorize("hasAuthority('SCOPE_inventory:read')")
  @GetMapping(value = "/", produces = APPLICATION_JSON_UTF8_VALUE)
  Stream<InventoryItemResponse> getAllInventoryItems() {
    return service.getAllInventoryItems();
  }

  @PreAuthorize("hasAuthority('SCOPE_inventory:read')")
//  @Secured("SCOPE_inventory:read")
  @GetMapping(value = "/{productCode}", produces = APPLICATION_JSON_UTF8_VALUE)
  Optional<InventoryItemResponse> findByProductCode(@PathVariable String productCode) {
    return service.findByProductCode(productCode);
  }

  @PreAuthorize("hasAuthority('SCOPE_inventory:write')")
//  @Secured("SCOPE_inventory:write")
  @PostMapping("/acquireExpiringLease")
//  @PostMapping("/leases")
  ExpiringInventoryLeaseRestResponse attemptExpiringLeaseAcquire(@RequestBody @Valid ExpiringInventoryLeaseRestRequest request) {
    return service.acquireLease(request);
  }

  @PreAuthorize("hasAuthority('SCOPE_inventory:write')")
//  @Secured("SCOPE_inventory:write")
  @DeleteMapping("/leases/{leaseId}")
  InventoryLeaseCancellationResponse cancelLease(@PathVariable int leaseId) {
    // TODO: Fix this
    return InventoryLeaseCancellationResponse.builder().cancelled(true).build();
  }

  @GetMapping("intentionallyErroringRemoteApiCall")
  void intentionallyErroringRemoteApiCall() throws InterruptedException {
    Thread.sleep(ThreadLocalRandom.current().nextInt(0, 2000));
    throw new RuntimeException("Intentional error from inventory service");
  }
}

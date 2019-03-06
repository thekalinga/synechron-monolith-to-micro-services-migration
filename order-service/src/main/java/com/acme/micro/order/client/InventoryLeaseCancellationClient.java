package com.acme.micro.order.client;

import com.acme.micro.order.client.contract.InventoryItemClientResponse;
import com.acme.micro.order.service.dto.InventoryLeaseCancellationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service", fallback = FallbackInventoryLeaseCancellationClient.class)
public interface InventoryLeaseCancellationClient {
  @GetMapping("/{productCode}")
  InventoryItemClientResponse getInventoryByProductCode(@PathVariable("productCode") String productCode);
  @DeleteMapping("/leases/{leaseId}")
  InventoryLeaseCancellationResponse cancel(int leaseId);
  @GetMapping("/intentionallyErroringRemoteApiCall")
  void intentionallyErroringRemoteApiCall();
}

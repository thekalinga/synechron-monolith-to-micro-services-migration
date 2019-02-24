package com.acme.micro.order.client;

import com.acme.micro.order.client.FallbackInventoryLeaseCancellationClient;
import com.acme.micro.order.service.dto.InventoryLeaseCancellationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service", fallback = FallbackInventoryLeaseCancellationClient.class)
public interface InventoryLeaseCancellationClient {
  @PostMapping("/cancelLease")
  InventoryLeaseCancellationResponse cancel();
  @GetMapping("/intentionallyErroringRemoteApiCall")
  void intentionallyErroringRemoteApiCall();
}

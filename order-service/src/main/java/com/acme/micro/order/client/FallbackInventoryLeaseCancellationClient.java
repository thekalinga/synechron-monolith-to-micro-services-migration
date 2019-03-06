package com.acme.micro.order.client;

import com.acme.micro.order.client.contract.InventoryItemClientResponse;
import com.acme.micro.order.service.dto.InventoryLeaseCancellationResponse;
import org.springframework.stereotype.Component;

@Component
public class FallbackInventoryLeaseCancellationClient implements InventoryLeaseCancellationClient {
  @Override
  public InventoryLeaseCancellationResponse cancel(int leaseId) {
    return InventoryLeaseCancellationResponse.builder().cancelled(false).build();
  }

  @Override
  public InventoryItemClientResponse getInventoryByProductCode(String productCode) {
    return null;
  }

  @Override
  public void intentionallyErroringRemoteApiCall() {
  }
}

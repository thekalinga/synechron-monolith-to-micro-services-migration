package com.acme.micro.order.client;

import com.acme.micro.order.service.dto.InventoryLeaseCancellationResponse;

public class FallbackInventoryLeaseCancellationClient implements InventoryLeaseCancellationClient {
  @Override
  public InventoryLeaseCancellationResponse cancel() {
    return InventoryLeaseCancellationResponse.builder().cancelled(false).build();
  }
}

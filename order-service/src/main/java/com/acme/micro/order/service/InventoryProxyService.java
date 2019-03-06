package com.acme.micro.order.service;

import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.micro.order.client.contract.InventoryItemClientResponse;

import java.util.List;

public interface InventoryProxyService {
  ExpiringInventoryLeaseRestResponse getExpiringLeaseForOrder(int orderId, String productCode, int quantity);
  boolean cancelLease();
  void intentionallyErroringRemoteApiCall();
  List<InventoryItemClientResponse> getInventories();
  InventoryItemClientResponse getInventoryByProductCode(String productCode);
}

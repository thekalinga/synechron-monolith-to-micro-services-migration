package com.acme.micro.order.service;

import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;

public interface InventoryProxyRestTemplateService {
  ExpiringInventoryLeaseRestResponse getExpiringLeaseForOrder(int orderId, String productCode, int quantity);
  boolean cancelLease();
  void intentionallyErroringRemoteApiCall();
}

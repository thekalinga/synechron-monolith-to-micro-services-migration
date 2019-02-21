package com.acme.micro.order.service;

import com.acme.micro.order.service.dto.ExpiringInventoryLeaseResponse;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public interface InventoryProxyRestTemplateService {
  ExpiringInventoryLeaseResponse getExpiringLeaseForOrder(int orderId, String productCode, int quantity);
  boolean confirmAcquire(int leaseId);
  boolean cancelLease(int leaseId);
}

package com.acme.monolith.service;

import com.acme.monolith.resource.dto.OrderRequest;
import com.acme.monolith.resource.dto.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderService {
  List<OrderResponse> getAllOrders();
  Optional<OrderResponse> getOrderById(int id);
  Optional<OrderResponse> placeOrder(OrderRequest request);
}

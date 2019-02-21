package com.acme.monolith.service;

import com.acme.monolith.domain.Order;
import com.acme.monolith.mapper.OrderMapper;
import com.acme.monolith.repository.OrderRepository;
import com.acme.monolith.repository.InventoryItemRepository;
import com.acme.monolith.resource.dto.OrderRequest;
import com.acme.monolith.resource.dto.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final InventoryItemRepository inventoryRepository;
  private final OrderMapper mapper;

  public OrderServiceImpl(OrderRepository orderRepository, InventoryItemRepository inventoryRepository, OrderMapper mapper) {
    this.orderRepository = orderRepository;
    this.inventoryRepository = inventoryRepository;
    this.mapper = mapper;
  }

  @Override
  public List<OrderResponse> getAllOrders() {
    return orderRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
  }

  @Override
  public Optional<OrderResponse> getOrderById(int id) {
    return orderRepository.findById(id).map(mapper::map);
  }

  @Override
  @Transactional
  public Optional<OrderResponse> placeOrder(OrderRequest request) {
    return inventoryRepository.findByProductCode(request.getProductCode())
        .filter(inventoryItem -> inventoryItem.getQuantity() >= request.getQuantity())
        .map(inventoryItem -> {
          inventoryItem.reduceQuantityBy(request.getQuantity());
          Order order = Order.builder().productCode(request.getProductCode()).productName(inventoryItem.getProductName()).quantity(request.getQuantity()).build();
          orderRepository.save(order);
          inventoryRepository.save(inventoryItem);
          return Optional.of(mapper.map(order));
        }).orElse(Optional.empty());
  }
}

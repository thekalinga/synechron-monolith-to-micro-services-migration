package com.acme.micro.order.repository;

import com.acme.micro.order.domain.Order;
import com.acme.micro.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
  List<Order> findAllByStatusAndLeaseValidTillBefore(OrderStatus status, LocalDateTime time);
}

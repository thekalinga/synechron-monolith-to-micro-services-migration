package com.acme.micro.order.service;

import com.acme.micro.order.domain.Order;
import com.acme.micro.order.mapper.OrderMapper;
import com.acme.micro.order.repository.OrderRepository;
import com.acme.micro.order.resource.dto.OrderRequest;
import com.acme.micro.order.resource.dto.OrderResponse;
import com.acme.micro.order.service.dto.ExpiringInventoryLeaseResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.acme.micro.order.domain.OrderStatus.cancelled_thru_compensation;
import static com.acme.micro.order.domain.OrderStatus.confirmed;
import static com.acme.micro.order.domain.OrderStatus.lease_acquired;
import static com.acme.micro.order.domain.OrderStatus.lease_could_not_be_acquired;
import static com.acme.micro.order.domain.OrderStatus.will_attempt_lease_acquire;
import static java.time.LocalDateTime.now;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final InventoryProxyRestTemplateService inventoryProxyRestTemplateService;
  private final OrderMapper mapper;
//  private final TransactionTemplate transactionTemplate;
//  private final EntityManager entityManager;

  public OrderServiceImpl(OrderRepository orderRepository,
      InventoryProxyRestTemplateService inventoryProxyRestTemplateService,
      OrderMapper mapper
//      TransactionTemplate transactionTemplate,
//      EntityManager entityManager
  ) {
    this.orderRepository = orderRepository;
    this.inventoryProxyRestTemplateService = inventoryProxyRestTemplateService;
    this.mapper = mapper;
//    this.transactionTemplate = transactionTemplate;
//    this.entityManager = entityManager;
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderResponse> getAllOrders() {
    return orderRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<OrderResponse> getOrderById(int id) {
    return orderRepository.findById(id).map(mapper::map);
  }

  @Override
  public Optional<OrderResponse> placeOrder(OrderRequest request) {
    Order order = Order.builder().productCode(request.getProductCode()).quantity(request.getQuantity()).status(will_attempt_lease_acquire).build();
    orderRepository.save(order);
    ExpiringInventoryLeaseResponse leaseResponse = inventoryProxyRestTemplateService.getExpiringLeaseForOrder(order.getId(), request.getProductCode(), request.getQuantity());
    order.setLeaseValidTill(leaseResponse.getLeaseValidTill());
    order.setStatus(leaseResponse.isLeaseAcquired() ? lease_acquired : lease_could_not_be_acquired);
    if (order.getStatus() == lease_acquired) {
      order.setProductName(leaseResponse.getProductName());
    }
    orderRepository.save(order);
    if (leaseResponse.isLeaseAcquired()) {
      boolean acquireConfirmed = inventoryProxyRestTemplateService.confirmAcquire(leaseResponse.getLeaseId());
      order.setStatus(acquireConfirmed ? confirmed : cancelled_thru_compensation);
      orderRepository.save(order);
      return Optional.of(mapper.map(order));
    } else {
      return Optional.empty();
    }
  }

  @Scheduled(fixedDelay = 1000L)
  void reconciler() {
    orderRepository.findAllByStatusAndLeaseValidTillBefore(will_attempt_lease_acquire, now()).forEach(order -> {
      try {
        order.setStatus(cancelled_thru_compensation);
        orderRepository.save(order);
      } catch (Exception e) {
        // intentionally ignoring with the assumption that db query is fine
        log.debug("Error occurred but ignored", e);
      }
    });
    orderRepository.findAllByStatusAndLeaseValidTillBefore(lease_acquired, now()).forEach(order -> {
      try {
        boolean leaseCancelled = inventoryProxyRestTemplateService.cancelLease(order.getId());
        order.setStatus(leaseCancelled ? cancelled_thru_compensation : confirmed);
        orderRepository.save(order);
      } catch (Exception e) {
        // intentionally ignoring with the assumption that db query is fine
        log.debug("Error occurred but ignored", e);
      }
    });
  }

}

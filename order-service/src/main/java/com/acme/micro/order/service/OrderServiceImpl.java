package com.acme.micro.order.service;

import com.acme.common.order.inventory.ConfirmLeaseCommand;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.common.order.inventory.LeaseConfirmationStatus;
import com.acme.common.order.inventory.LeaseConfirmationStatusEvent;
import com.acme.micro.order.client.contract.InventoryItemClientResponse;
import com.acme.micro.order.domain.Order;
import com.acme.micro.order.integration.InventoryBinding;
import com.acme.micro.order.mapper.OrderMapper;
import com.acme.micro.order.repository.OrderRepository;
import com.acme.micro.order.resource.contract.OrderRequest;
import com.acme.micro.order.resource.contract.OrderResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.acme.common.order.inventory.LeaseAcquisitionStatus.acquired;
import static com.acme.common.order.inventory.OrderInventoryIntegrationConstants.LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME;
import static com.acme.micro.order.domain.OrderStatus.confirmed;
import static com.acme.micro.order.domain.OrderStatus.lease_acquired;
import static com.acme.micro.order.domain.OrderStatus.lease_could_not_be_acquired;
import static com.acme.micro.order.domain.OrderStatus.lease_expired;
import static com.acme.micro.order.domain.OrderStatus.unexpected_error_while_confirming_lease;
import static com.acme.micro.order.domain.OrderStatus.will_attempt_lease_acquire;
import static java.time.LocalDateTime.now;

@Log4j2
@Service
@EnableBinding(InventoryBinding.class)
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final InventoryProxyService inventoryProxyService;
  private final OrderMapper mapper;
  private final InventoryBinding inventoryBinding;

  public OrderServiceImpl(OrderRepository orderRepository, InventoryProxyService inventoryProxyService, OrderMapper mapper, InventoryBinding inventoryBinding) {
    this.orderRepository = orderRepository;
    this.inventoryProxyService = inventoryProxyService;
    this.mapper = mapper;
    this.inventoryBinding = inventoryBinding;
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
    log.debug("Received order {}", request);
    Order order = Order.builder().productCode(request.getProductCode()).quantity(request.getQuantity()).status(will_attempt_lease_acquire).build();
    orderRepository.save(order);
    ExpiringInventoryLeaseRestResponse leaseResponse = inventoryProxyService
        .getExpiringLeaseForOrder(order.getId(), request.getProductCode(), request.getQuantity());
    order.setLeaseId(leaseResponse.getLeaseId());
    order.setLeaseValidTill(leaseResponse.getLeaseValidTill());
    order.setStatus(leaseResponse.getStatus() == acquired ? lease_acquired : lease_could_not_be_acquired);
    if (order.getStatus() == lease_acquired) {
      order.setProductName(leaseResponse.getProductName());
      log.debug("Lease is acquired");
    }
    orderRepository.save(order);
    if (leaseResponse.getStatus() == acquired) {
      Message<ConfirmLeaseCommand> message = MessageBuilder
          .withPayload(ConfirmLeaseCommand.builder().leaseId(leaseResponse.getLeaseId()).build())
          .build();
      inventoryBinding.leaseConfirmationCommandChannel().send(message);
      log.debug("Sent lease confirmation request {} to inventory service via kafka", message);
      return Optional.of(mapper.map(order));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void intentionallyErroringRemoteApiCall() {
    inventoryProxyService.intentionallyErroringRemoteApiCall();
  }

  @Override
  public List<InventoryItemClientResponse> getInventories() {
    return inventoryProxyService.getInventories();
  }

  @Override
  public InventoryItemClientResponse getInventoryByProductCode(String productCode) {
    return inventoryProxyService.getInventoryByProductCode(productCode);
  }

  @StreamListener
  void handleLeaseConfirmationUpdates(@Input(LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME) KStream<Integer, LeaseConfirmationStatusEvent> leaseConfirmationStatusStream) {
    leaseConfirmationStatusStream
        .foreach((key, event) -> {
          log.debug("Received lease confirmation response {} with key {} to inventory service via kafka", event, key);
          orderRepository.findByLeaseId(event.getLeaseId())
              .ifPresent(order -> {
                LeaseConfirmationStatus status = event.getConfirmationStatus();
                switch (status) {
                  case lease_expired:
                    order.setStatus(lease_expired);
                    break;
                  case invalid_lease:
                    order.setStatus(unexpected_error_while_confirming_lease);
                    break;
                  case lease_confirmed:
                    order.setStatus(confirmed);
                    break;
                }
                orderRepository.save(order);
              });
          log.debug("Lease confirmation processed successfully");
        });
  }

//  @Scheduled(fixedDelay = 60 * 1000L)
//  void reconciler() {
//    log.debug("Reconciler triggered");
//    orderRepository.findAllByStatusAndLeaseValidTillBefore(will_attempt_lease_acquire, now()).forEach(order -> {
//      try {
//        order.setStatus(cancelled_thru_compensation);
//        orderRepository.save(order);
//      } catch (Exception e) {
//        // intentionally ignoring with the assumption that db query is fine
//        log.debug("Error occurred but ignored", e);
//      }
//    });
//    orderRepository.findAllByStatusAndLeaseValidTillBefore(lease_acquired, now()).forEach(order -> {
//      try {
//        boolean leaseCancelled = inventoryProxyService.cancelLease();
//        order.setStatus(leaseCancelled ? cancelled_thru_compensation : confirmed);
//        orderRepository.save(order);
//      } catch (Exception e) {
//        // intentionally ignoring with the assumption that db query is fine
//        log.debug("Error occurred but ignored", e);
//      }
//    });
//  }

}

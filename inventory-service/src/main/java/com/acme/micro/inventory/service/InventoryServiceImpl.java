package com.acme.micro.inventory.service;

import com.acme.common.order.inventory.ConfirmLeaseCommand;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestRequest;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.common.order.inventory.LeaseAcquisitionStatus;
import com.acme.common.order.inventory.LeaseConfirmationStatusEvent;
import com.acme.micro.inventory.binding.OrderBinding;
import com.acme.micro.inventory.domain.InventoryItem;
import com.acme.micro.inventory.domain.InventoryItemLease;
import com.acme.micro.inventory.repository.InventoryItemLeaseRepository;
import com.acme.micro.inventory.repository.InventoryItemRepository;
import com.acme.micro.inventory.repository.LeaseStatus;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;

import static com.acme.common.order.inventory.LeaseAcquisitionStatus.acquired;
import static com.acme.common.order.inventory.LeaseAcquisitionStatus.insufficient_quantity_available;
import static com.acme.common.order.inventory.LeaseAcquisitionStatus.invalid_product_code;
import static com.acme.common.order.inventory.LeaseConfirmationStatus.invalid_lease;
import static com.acme.common.order.inventory.OrderInventoryIntegrationConstants.LEASE_CONFIRMATION_REQUEST_DESTINATION_NAME;
import static com.acme.common.order.inventory.OrderInventoryIntegrationConstants.LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME;
import static com.acme.micro.inventory.repository.LeaseStatus.confirmed;
import static com.acme.micro.inventory.repository.LeaseStatus.expired;
import static com.acme.micro.inventory.repository.LeaseStatus.pending;
import static java.time.LocalDateTime.now;

@Log4j2
@EnableBinding(OrderBinding.class)
@Transactional
public class InventoryServiceImpl implements InventoryService {

  private final InventoryItemRepository repository;
  private final InventoryItemLeaseRepository leaseRepository;
  private final TransactionTemplate transactionTemplate;
  private final EntityManager entityManager;

  public InventoryServiceImpl(InventoryItemRepository itemRepository, InventoryItemLeaseRepository leaseRepository,
      TransactionTemplate transactionTemplate, EntityManager entityManager) {
    this.repository = itemRepository;
    this.leaseRepository = leaseRepository;
    this.transactionTemplate = transactionTemplate;
    this.entityManager = entityManager;
  }

  @Override
  public ExpiringInventoryLeaseRestResponse acquireLease(ExpiringInventoryLeaseRestRequest request) {
    log.debug("Received acquire lease request for {}", request);
    return leaseRepository.findByOrderId(request.getOrderId())
        .map(lease -> { // incase if we failed to send
          return repository.findByProductCode(request.getProductCode())
              .map(item -> toExpiringInventoryLeaseRestResponse(item, lease, acquired))
              .orElse(ExpiringInventoryLeaseRestResponse.builder().status(invalid_product_code).build());
        })
        .orElseGet(() -> {
          return repository.findByProductCode(request.getProductCode())
              .map(item -> {
                int existingQuantity = item.getQuantity();
                if (existingQuantity >= request.getQuantity()) {
                  InventoryItemLease lease = InventoryItemLease.builder().item(item).orderId(request.getOrderId())
                      .status(pending).quantity(request.getQuantity())
                      .validTill(now().plusMinutes(2)).build();
                  try {
                    item.reduceQuantityBy(request.getQuantity());
                    repository.save(item);
                    leaseRepository.save(lease);
                    ExpiringInventoryLeaseRestResponse response = toExpiringInventoryLeaseRestResponse(item, lease, acquired);
                    log.debug("Lease request saved & am returning {}", response);
                    return response;
                  } catch (DataIntegrityViolationException e) { // We processed this event before (may be we died earlier & are reprocessing the same event). Lets just return existing lease
                    return leaseRepository.findByOrderId(lease.getOrderId())
                        .map(existingLease -> toExpiringInventoryLeaseRestResponse(item, lease, acquired))
                        .orElse(ExpiringInventoryLeaseRestResponse.builder().status(invalid_product_code).build());
                  }
                } else {
                  return ExpiringInventoryLeaseRestResponse.builder().status(insufficient_quantity_available).build();
                }
              })
              .orElse(ExpiringInventoryLeaseRestResponse.builder().status(invalid_product_code).build());
        });
  }

  @StreamListener
  @SendTo(LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME)
  public KStream<Integer, LeaseConfirmationStatusEvent> leaseConfirmationCommandProcessor(@Input(LEASE_CONFIRMATION_REQUEST_DESTINATION_NAME) KStream<Integer, ConfirmLeaseCommand> leaseConfirmationCommandStream) {
    return leaseConfirmationCommandStream.map((key, command) -> {
      log.debug("Received message key {}, command {}", key, command);
      return leaseRepository.findById(command.getLeaseId()).map(lease -> {
        log.debug("Lease id {} found", command.getLeaseId());
        LeaseStatus status = lease.getStatus();
        if (lease.getValidTill().isBefore(now())) { // lease is expired, lets revert the old reservation
          // since we are operating inside the callsbacks spring's AOP based transaction management will not be useful here, which is why we are explicitly using transactions
          status = transactionTemplate.execute(__ -> {
            log.debug("Lease is expired. Will attempt to add {} more items to {}", lease.getQuantity(), lease.getItem().getProductName());
            lease.setStatus(expired);
            lease.getItem().increaseQuantityBy(lease.getQuantity());
            repository.save(lease.getItem());
            leaseRepository.save(lease);
            return expired;
          });

          log.debug("Expired reservation quantity is restored successfully");
        } else if (status == pending) {
          status = confirmed;
          lease.setStatus(status);
          leaseRepository.save(lease);
          log.debug("Reservation confirmed successfully");
        }
        // since we are an idempotent consumer, we should tolerate atleas once deliverty semantics. Which is why we are not throwing any error incase if we receive an event for already confirmed leaseId
        LeaseConfirmationStatusEvent statusChangedEvent = LeaseConfirmationStatusEvent.builder().leaseId(command.getLeaseId()).confirmationStatus(status.toConfirmationStatus()).build();
        log.debug("Publishing response event {}", statusChangedEvent);
        return new KeyValue<>(command.getLeaseId(), statusChangedEvent);
      })
      .orElse(new KeyValue<>(command.getLeaseId(), LeaseConfirmationStatusEvent.builder().leaseId(command.getLeaseId()).confirmationStatus(invalid_lease).build()));
    });
  }

  @Scheduled(fixedDelay = 60 * 1000)
  void expiredLeaseReleaseTask() {
    log.debug("Cleaup of expired lease started");
    leaseRepository.findAllByValidTillBeforeAndStatus(now(), pending)
        .forEach(itemLease -> {
          log.debug("Expired reservation quantity is restored successfully for {}", itemLease);
          itemLease.setStatus(expired);
          itemLease.getItem().increaseQuantityBy(itemLease.getQuantity());
          repository.save(itemLease.getItem());
          leaseRepository.save(itemLease);
        });
    log.debug("Cleaup of expired leases finished");
  }

  private static ExpiringInventoryLeaseRestResponse toExpiringInventoryLeaseRestResponse(InventoryItem item, InventoryItemLease lease, LeaseAcquisitionStatus status) {
    return ExpiringInventoryLeaseRestResponse.builder().leaseId(lease.getId()).leaseValidTill(lease.getValidTill()).productName(item.getProductName()).status(status).build();
  }

}

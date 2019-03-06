package com.acme.micro.inventory.service;

import com.acme.common.order.inventory.ConfirmLeaseCommand;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestRequest;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.common.order.inventory.LeaseConfirmationStatusEvent;
import com.acme.micro.inventory.resource.contract.InventoryItemResponse;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Optional;
import java.util.stream.Stream;

public interface InventoryService {
  Stream<InventoryItemResponse> getAllInventoryItems();
  Optional<InventoryItemResponse> findByProductCode(String productCode);
  ExpiringInventoryLeaseRestResponse acquireLease(ExpiringInventoryLeaseRestRequest request);
  KStream<Integer, LeaseConfirmationStatusEvent> leaseConfirmationCommandProcessor(KStream<Integer, ConfirmLeaseCommand> leaseConfirmationCommandStream);
}

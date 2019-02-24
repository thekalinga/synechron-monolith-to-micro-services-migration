package com.acme.micro.inventory.service;

import com.acme.common.order.inventory.ConfirmLeaseCommand;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestRequest;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.common.order.inventory.LeaseConfirmationStatusEvent;
import org.apache.kafka.streams.kstream.KStream;

public interface InventoryService {
  ExpiringInventoryLeaseRestResponse acquireLease(ExpiringInventoryLeaseRestRequest request);
  KStream<Integer, LeaseConfirmationStatusEvent> leaseConfirmationCommandProcessor(KStream<Integer, ConfirmLeaseCommand> leaseConfirmationCommandStream);
}

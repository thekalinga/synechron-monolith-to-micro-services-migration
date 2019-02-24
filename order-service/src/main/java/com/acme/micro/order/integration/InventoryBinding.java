package com.acme.micro.order.integration;

import com.acme.common.order.inventory.LeaseConfirmationStatusEvent;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import static com.acme.common.order.inventory.OrderInventoryIntegrationConstants.LEASE_CONFIRMATION_REQUEST_DESTINATION_NAME;
import static com.acme.common.order.inventory.OrderInventoryIntegrationConstants.LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME;

public interface InventoryBinding {
  @Output(LEASE_CONFIRMATION_REQUEST_DESTINATION_NAME)
  MessageChannel leaseConfirmationCommandChannel();
  @Input(LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME)
  KStream<Integer, LeaseConfirmationStatusEvent> leaseConfirmationConsumer();
}

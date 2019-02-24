package com.acme.micro.inventory.binding;

import com.acme.common.order.inventory.ConfirmLeaseCommand;
import com.acme.common.order.inventory.LeaseConfirmationStatusEvent;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

import static com.acme.common.order.inventory.OrderInventoryIntegrationConstants.LEASE_CONFIRMATION_REQUEST_DESTINATION_NAME;
import static com.acme.common.order.inventory.OrderInventoryIntegrationConstants.LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME;

public interface OrderBinding {
  @Output(LEASE_CONFIRMATION_RESPONSE_DESTINATION_NAME)
  KStream<Integer, LeaseConfirmationStatusEvent> leaseConfirmationCommandChannel();
  @Input(LEASE_CONFIRMATION_REQUEST_DESTINATION_NAME)
  KStream<Integer, ConfirmLeaseCommand> leaseConfirmationConsumer();
}

package com.acme.common.order.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LeaseConfirmationStatusEvent {
  private int leaseId;
  private LeaseConfirmationStatus confirmationStatus;
}

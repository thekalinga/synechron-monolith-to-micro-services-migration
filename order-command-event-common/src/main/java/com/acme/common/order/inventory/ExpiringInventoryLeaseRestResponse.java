package com.acme.common.order.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpiringInventoryLeaseRestResponse {
  private LeaseAcquisitionStatus status;
  private int leaseId;
  private String productName;
  private LocalDateTime leaseValidTill;
}

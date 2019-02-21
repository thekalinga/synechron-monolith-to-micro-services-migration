package com.acme.micro.inventory.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExpiringInventoryLeaseResponse {
  private boolean leaseAcquired;
  private int leaseId;
  private String productName;
  private LocalDateTime leaseValidTill;
}

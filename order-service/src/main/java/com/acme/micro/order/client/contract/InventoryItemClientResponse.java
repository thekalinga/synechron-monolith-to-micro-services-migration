package com.acme.micro.order.client.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemClientResponse {
  private int id;
  private String productName;
  private String productCode;
  private int quantity;
}

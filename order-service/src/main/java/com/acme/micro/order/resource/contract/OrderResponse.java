package com.acme.micro.order.resource.contract;

import com.acme.micro.order.domain.OrderStatus;
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
public class OrderResponse {
  private int id;
  private String productName;
  private int quantity;
  private OrderStatus status;
}

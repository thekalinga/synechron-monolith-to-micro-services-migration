package com.acme.micro.order.mapper;

import com.acme.micro.order.domain.Order;
import com.acme.micro.order.resource.contract.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  OrderResponse map(Order order);
}

package com.acme.monolith.mapper;

import com.acme.monolith.domain.Order;
import com.acme.monolith.resource.dto.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  OrderResponse map(Order order);
}

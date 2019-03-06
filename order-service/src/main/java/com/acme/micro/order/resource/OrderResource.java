package com.acme.micro.order.resource;

import com.acme.micro.order.client.contract.InventoryItemClientResponse;
import com.acme.micro.order.common.TunableProperties;
import com.acme.micro.order.resource.contract.OrderRequest;
import com.acme.micro.order.resource.contract.OrderResponse;
import com.acme.micro.order.service.OrderService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@EnableConfigurationProperties(TunableProperties.class)
public class OrderResource {

  private final OrderService service;
  private final TunableProperties tunableProperties;

  public OrderResource(OrderService service, TunableProperties tunableProperties) {
    this.service = service;
    this.tunableProperties = tunableProperties;
  }

  @PreAuthorize("hasAuthority('SCOPE_order:read')")
//  @Secured("SCOPE_order:read")
  @GetMapping("/inventories")
  List<InventoryItemClientResponse> inventories() {
    return service.getInventories();
  }

  @PreAuthorize("hasAuthority('SCOPE_order:read')")
//  @Secured("SCOPE_order:read")
  @GetMapping("/inventories/{productCode}")
  InventoryItemClientResponse inventoryByProductCode(@PathVariable String productCode) {
    return service.getInventoryByProductCode(productCode);
  }

  @PreAuthorize("hasAuthority('SCOPE_order:read')")
//  @Secured("SCOPE_order:read")
  @GetMapping("/orders")
  List<OrderResponse> listOrders() throws InterruptedException {
    Thread.sleep(tunableProperties.getSleepTimeInMillis());
    return service.getAllOrders();
  }

  @PreAuthorize("hasAuthority('SCOPE_order:read')")
//  @Secured("SCOPE_order:read")
  @GetMapping("/orders/{id}")
  ResponseEntity<OrderResponse> getOrderById(@PathVariable int id) {
    return service.getOrderById(id)
        .map(orderResponse -> ResponseEntity.ok().body(orderResponse))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PreAuthorize("hasAuthority('SCOPE_order:write')")
//  @Secured("SCOPE_order:write")
  @PostMapping("/orders")
  ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
    return service.placeOrder(request)
        .map(orderResponse -> ResponseEntity.ok().body(orderResponse))
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping("/intentionalError")
  void intentionallyErroringRemoteApiCall() {
    service.intentionallyErroringRemoteApiCall();
  }

}

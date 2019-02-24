package com.acme.micro.order.resource;

import com.acme.micro.order.resource.contract.OrderRequest;
import com.acme.micro.order.resource.contract.OrderResponse;
import com.acme.micro.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping
public class OrderResource {

  private final OrderService service;

  public OrderResource(OrderService service) {
    this.service = service;
  }

  @GetMapping
  List<OrderResponse> listOrders() {
    if (ThreadLocalRandom.current().nextInt(1, 10) > 3) {
      return service.getAllOrders();
    } else {
      throw new RuntimeException("Intentionally throwing exceptions");
    }
  }

  @GetMapping("/{id}")
  ResponseEntity<OrderResponse> getOrderById(@PathVariable int id) {
    return service.getOrderById(id)
        .map(orderResponse -> ResponseEntity.ok().body(orderResponse))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
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

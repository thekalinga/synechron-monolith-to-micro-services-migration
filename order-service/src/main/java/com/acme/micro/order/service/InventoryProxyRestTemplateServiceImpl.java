package com.acme.micro.order.service;

import com.acme.micro.order.service.dto.ExpiringInventoryLeaseRequest;
import com.acme.micro.order.service.dto.ExpiringInventoryLeaseResponse;
import com.acme.micro.order.service.dto.IdContainer;
import com.acme.micro.order.service.dto.InventoryAcquireConfirmationResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpMethod.POST;

@Service
public class InventoryProxyRestTemplateServiceImpl implements InventoryProxyRestTemplateService {
  private final RestTemplate restTemplate;
  private final DiscoveryClient discoveryClient;
  private final InventoryLeaseCancellationClient leaseCancellationClient;
  private final LoadBalancerClient loadBalancerClient;

  public InventoryProxyRestTemplateServiceImpl(RestTemplate restTemplate, DiscoveryClient discoveryClient, InventoryLeaseCancellationClient leaseCancellationClient, LoadBalancerClient loadBalancerClient) {
    this.restTemplate = restTemplate;
    this.discoveryClient = discoveryClient;
    this.leaseCancellationClient = leaseCancellationClient;
    this.loadBalancerClient = loadBalancerClient;
  }

  @Override
  @HystrixCommand(fallbackMethod = "fallbackForErroringExpiringLeaseForOrder")
  public ExpiringInventoryLeaseResponse getExpiringLeaseForOrder(int orderId,
      String productCode, int quantity) {
    ExpiringInventoryLeaseRequest request =
        ExpiringInventoryLeaseRequest.builder().orderId(orderId).productCode(productCode)
            .quantity(quantity).build();
    URI uri = UriComponentsBuilder.fromHttpUrl("http://inventory-service/acquireExpiringLease").build().toUri();
    RequestEntity<ExpiringInventoryLeaseRequest> requestEntity = new RequestEntity<>(request, POST, uri);
    return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<ExpiringInventoryLeaseResponse>() {}).getBody();
  }

  // Since we want to mark tx as cancelled, lets just do that
  public ExpiringInventoryLeaseResponse fallbackForErroringExpiringLeaseForOrder(int orderId, String productCode, int quantity, Throwable e) {
    return ExpiringInventoryLeaseResponse.builder().leaseAcquired(false).build();
  }

  @Override
  public boolean confirmAcquire(int leaseId) {
    List<ServiceInstance> instances = discoveryClient.getInstances("inventory-service");
//    loadBalancerClient.execute("inventory-service", new LoadBalancerRequest<IdContainer>() {
//      @Override
//      public IdContainer apply(ServiceInstance instance) throws Exception {
//        return instance.;
//      }
//    })
    if (instances.size() > 0) {
      ServiceInstance serviceInstance = instances.get(0);
      URI uri = UriComponentsBuilder.fromUri(serviceInstance.getUri()).path("confirmAcquire").build().toUri();
      RestTemplate restTemplate = new RestTemplate();
      IdContainer idContainer = IdContainer.builder().id(leaseId).build();
      RequestEntity<IdContainer> requestEntity = new RequestEntity<>(idContainer, POST, uri);
      return Objects.requireNonNull(restTemplate.exchange(requestEntity, new ParameterizedTypeReference<InventoryAcquireConfirmationResponse>() {}).getBody()).isAcquired();
    }
    return false;
  }

  @Override
  public boolean cancelLease(int leaseId) {
    return leaseCancellationClient.cancel(leaseId).isCancelled();
  }
}

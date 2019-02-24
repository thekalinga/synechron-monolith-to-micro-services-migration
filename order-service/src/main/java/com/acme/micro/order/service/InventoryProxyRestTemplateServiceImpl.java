package com.acme.micro.order.service;

import com.acme.common.order.inventory.ExpiringInventoryLeaseRestRequest;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.common.order.inventory.LeaseAcquisitionStatus;
import com.acme.micro.order.client.InventoryLeaseCancellationClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.acme.common.order.inventory.LeaseAcquisitionStatus.unknown_error;
import static org.springframework.http.HttpMethod.POST;

@Log4j2
@Service
public class InventoryProxyRestTemplateServiceImpl implements InventoryProxyRestTemplateService {
  private final RestTemplate restTemplate;
  private final InventoryLeaseCancellationClient leaseCancellationClient;

  @SuppressWarnings("unused")
  public InventoryProxyRestTemplateServiceImpl(RestTemplate restTemplate, InventoryLeaseCancellationClient leaseCancellationClient) {
    this.restTemplate = restTemplate;
    this.leaseCancellationClient = leaseCancellationClient;
  }

  @Override
  @HystrixCommand(fallbackMethod = "fallbackForErroringExpiringLeaseForOrder")
  public ExpiringInventoryLeaseRestResponse getExpiringLeaseForOrder(int orderId, String productCode, int quantity) {
    log.debug("Will be requesting expiring lease for a quantity of {} for order {} & product code {}", quantity, orderId, productCode);
    ExpiringInventoryLeaseRestRequest request = ExpiringInventoryLeaseRestRequest.builder().orderId(orderId).productCode(productCode).quantity(quantity).build();
    URI uri = UriComponentsBuilder.fromHttpUrl("http://inventory-service/acquireExpiringLease").build().toUri();
    RequestEntity<ExpiringInventoryLeaseRestRequest> requestEntity = new RequestEntity<>(request, POST, uri);
    return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<ExpiringInventoryLeaseRestResponse>() {}).getBody();
  }

  // Since we want to mark tx as cancelled, lets just do that
  public ExpiringInventoryLeaseRestResponse fallbackForErroringExpiringLeaseForOrder(int orderId, String productCode, int quantity, Throwable e) {
    log.debug("Returing to fallback due to error", e);
    return ExpiringInventoryLeaseRestResponse.builder().status(unknown_error).build();
  }

  @Override
  public boolean cancelLease() {
    log.debug("Will be requesting cancelling lease");
    return leaseCancellationClient.cancel().isCancelled();
  }

  @Override
  public void intentionallyErroringRemoteApiCall() {
    log.debug("Attempting to request an API that throws an error intentionally");
    leaseCancellationClient.intentionallyErroringRemoteApiCall();
  }
}

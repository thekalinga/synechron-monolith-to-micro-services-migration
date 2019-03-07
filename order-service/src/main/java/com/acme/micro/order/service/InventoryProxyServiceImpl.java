package com.acme.micro.order.service;

import com.acme.common.order.inventory.ExpiringInventoryLeaseRestRequest;
import com.acme.common.order.inventory.ExpiringInventoryLeaseRestResponse;
import com.acme.micro.order.client.InventoryLeaseCancellationClient;
import com.acme.micro.order.client.contract.InventoryItemClientResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.acme.common.order.inventory.LeaseAcquisitionStatus.unknown_error;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Log4j2
@Service
public class InventoryProxyServiceImpl implements InventoryProxyService {
  private final WebClient.Builder webClientBuilder;
  //  private final WebClient webClient;
  private final InventoryLeaseCancellationClient leaseCancellationClient;
  private final CachingOAuth2AuthorizedClientService authorizedClientService;

  @SuppressWarnings("unused")
  public InventoryProxyServiceImpl(WebClient.Builder webClientBuilder,
      InventoryLeaseCancellationClient leaseCancellationClient,
      CachingOAuth2AuthorizedClientService authorizedClientService) {
    this.webClientBuilder = webClientBuilder;
    // TODO: LoadBalancerExchangeFilterFunction is not being added a filter at injection time. Its evaluated lazily. Need to check if there is alternative way to get this lazily. Does `@Lazy` work?
//    this.webClient = webClientBuilder.build();
    this.leaseCancellationClient = leaseCancellationClient;
    this.authorizedClientService = authorizedClientService;
  }

  @Override
  @HystrixCommand(fallbackMethod = "fallbackForErroringExpiringLeaseForOrder")
  public ExpiringInventoryLeaseRestResponse getExpiringLeaseForOrder(int orderId,
      String productCode, int quantity) {
    log.debug(
        "Will be requesting expiring lease for a quantity of {} for order {} & product code {}",
        quantity, orderId, productCode);
    ExpiringInventoryLeaseRestRequest request =
        ExpiringInventoryLeaseRestRequest.builder().orderId(orderId).productCode(productCode)
            .quantity(quantity).build();

    //.uri("http://inventory-service/leases")
    return webClientBuilder.build().post().uri("http://inventory-service/acquireExpiringLease")
        .attributes(oauth2AuthorizedClient(getCurrentOAuth2AuthorizedClient()))
        .syncBody(request)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<ExpiringInventoryLeaseRestResponse>() {})
        .block();
    //    return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<ExpiringInventoryLeaseRestResponse>() {}).getBody();
  }

  private OAuth2AuthorizedClient getCurrentOAuth2AuthorizedClient() {
    return authorizedClientService.findAuthenticatedClientByClientCredentials("inventory-service-client")
        .orElseThrow(() -> new IllegalStateException("Invalid client credentials"));
  }

  // Since we want to mark tx as cancelled, lets just do that
  public ExpiringInventoryLeaseRestResponse fallbackForErroringExpiringLeaseForOrder(int orderId,
      String productCode, int quantity, Throwable e) {
    log.debug("Returing to fallback due to error", e);
    return ExpiringInventoryLeaseRestResponse.builder().status(unknown_error).build();
  }

  @Override
  public boolean cancelLease() {
    log.debug("Will be requesting cancelling lease");
    // TODO: Fix this
    return leaseCancellationClient.cancel(1).isCancelled();
  }

  @Override
  public void intentionallyErroringRemoteApiCall() {
    log.debug("Attempting to request an API that throws an error intentionally");
    leaseCancellationClient.intentionallyErroringRemoteApiCall();
  }

  @Override
  @HystrixCommand
  public List<InventoryItemClientResponse> getInventories() {
    return webClientBuilder.build().get().uri("http://inventory-service")
        .attributes(oauth2AuthorizedClient(getCurrentOAuth2AuthorizedClient()))
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<InventoryItemClientResponse>>() {})
        .block();
  }

  @Override
  public InventoryItemClientResponse getInventoryByProductCode(String productCode) {
    return leaseCancellationClient.getInventoryByProductCode(productCode);
  }
}

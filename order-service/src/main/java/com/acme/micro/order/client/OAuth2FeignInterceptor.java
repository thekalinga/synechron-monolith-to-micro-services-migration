package com.acme.micro.order.client;

import com.acme.micro.order.service.CachingOAuth2AuthorizedClientService;
import com.acme.micro.order.service.CachingOAuth2AuthorizedClientServiceImpl;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;

@Component
public class OAuth2FeignInterceptor implements RequestInterceptor {

  public static final String BEARER_TOKEN_TYPE = "Bearer";
  public static final String AUTHORIZATION_HEADER = "Authorization";
  private final CachingOAuth2AuthorizedClientService authorizedClientService;

  public OAuth2FeignInterceptor(CachingOAuth2AuthorizedClientService authorizedClientService) {
    this.authorizedClientService = authorizedClientService;
  }

  @Override
  public void apply(RequestTemplate requestTemplate) {
    OAuth2AuthorizedClient authorizedClient =
        authorizedClientService.findAuthenticatedClientByClientCredentials("inventory-service-client")
            .orElseThrow(() -> new IllegalStateException("Invalid client credentials"));

    requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, authorizedClient.getAccessToken().getTokenValue()));
  }
}

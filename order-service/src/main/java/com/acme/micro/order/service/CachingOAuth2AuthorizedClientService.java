package com.acme.micro.order.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import java.util.Optional;

public interface CachingOAuth2AuthorizedClientService {
  Optional<OAuth2AuthorizedClient> findAuthenticatedClientByClientCredentials(
      String clientRegistrationId);
}

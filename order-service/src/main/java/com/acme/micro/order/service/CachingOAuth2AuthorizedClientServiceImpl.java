package com.acme.micro.order.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CachingOAuth2AuthorizedClientServiceImpl
    implements CachingOAuth2AuthorizedClientService {

  private Map<String, Optional<OAuth2AuthorizedClient>> clientIdToAuthorizedClient = new ConcurrentHashMap<>();

  private final ClientRegistrationRepository clientRegistrationRepository;

  private final OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> clientCredentialsTokenResponseClient = new DefaultClientCredentialsTokenResponseClient();

  public CachingOAuth2AuthorizedClientServiceImpl(
      ClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Override
  public Optional<OAuth2AuthorizedClient> findAuthenticatedClientByClientCredentials(
      String clientRegistrationId) {
    // TODO: Refresh token if already expired. Refer to `ServerOAuth2AuthorizedClientExchangeFilterFunction` on how to do this
      return clientIdToAuthorizedClient.computeIfAbsent(clientRegistrationId, clientId -> {
          ClientRegistration clientRegistration =
              this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
          if (clientRegistration == null) {
            return Optional.empty();
          }
          OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest =
              new OAuth2ClientCredentialsGrantRequest(clientRegistration);
          OAuth2AccessTokenResponse tokenResponse = this.clientCredentialsTokenResponseClient
              .getTokenResponse(clientCredentialsGrantRequest);

          OAuth2AuthorizedClient oAuth2AuthorizedClient =
              new OAuth2AuthorizedClient(clientRegistration, clientId,
                  tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
          return Optional.of(oAuth2AuthorizedClient);
        });
  }

}

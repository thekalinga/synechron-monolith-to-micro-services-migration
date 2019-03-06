package com.acme.micro.order.config;

import brave.propagation.CurrentTraceContext;
import brave.propagation.ThreadLocalCurrentTraceContext;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CommonConfiguration {
  //  @Bean
//  @LoadBalanced
//  RestTemplate loadBalancedRestTemplate(RestTemplateBuilder builder) {
//    return builder.build();
//  }

    @Bean
    @LoadBalanced
    WebClient.Builder loadBalancedWebClient(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {
      ServletOAuth2AuthorizedClientExchangeFilterFunction authorizedClientExchangeFilterFunction =
          new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
      authorizedClientExchangeFilterFunction.setDefaultClientRegistrationId("inventory-service-client");
      return WebClient.builder().apply(authorizedClientExchangeFilterFunction.oauth2Configuration());
    }

//  @Bean
//  CurrentTraceContext log4jTraceContext() {
//    return ThreadLocalCurrentTraceContext.newBuilder().build();
//  }
}

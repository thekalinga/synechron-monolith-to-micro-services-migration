package com.acme.oauth2.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@SpringBootApplication
public class OAuth2ClientApplication {
  public static void main(String[] args) {
    SpringApplication.run(OAuth2ClientApplication.class, args);
  }
}


@Configuration
class ClientConfiguration {
  @Bean
  WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {
    ServletOAuth2AuthorizedClientExchangeFilterFunction authorizedClientExchangeFilterFunction =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
            authorizedClientRepository);
    return WebClient.builder().apply(authorizedClientExchangeFilterFunction.oauth2Configuration()).build();
  }
}

@Configuration
@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final KeycloakLogoutHandler logoutHandler;

  public SecurityConfiguration(KeycloakLogoutHandler logoutHandler) {
    this.logoutHandler = logoutHandler;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
          .anyRequest().authenticated()
          .and()
        .oauth2Login()
          .loginPage("/oauth2/authorization/keycloak")
          .failureUrl("/login?error")
          .permitAll()
          .and()
        .oauth2Client()
          .and()
        .logout()
          .logoutUrl("/logout")
          .addLogoutHandler(logoutHandler)
          .and()
        .addFilter(new DefaultLogoutPageGeneratingFilter()); // by default as soon as we customize `loginPage`, spring security does not configure bth login and logout pages. So, we are just adding logout filter explicitly that generates default logout page
  }
}


@RestController
class UserResource {
  private final WebClient webClient;

  public UserResource(WebClient webClient) {
    this.webClient = webClient;
  }

  @GetMapping(value = "/userinfo", produces = APPLICATION_JSON_UTF8_VALUE)
  Map<String, Object> getAllUsers(OAuth2AuthenticationToken token) {
    return token.getPrincipal().getAttributes();
  }

  @GetMapping(value = "/resource", produces = APPLICATION_JSON_UTF8_VALUE)
  Map<String, Object> getAllUsers(OAuth2AuthorizedClient client) {
    return webClient.get()
        .uri("http://localhost:18080")
        .attributes(oauth2AuthorizedClient(client))
        .accept(APPLICATION_JSON_UTF8)
        .exchange()
        .flatMap(clientResponse -> clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
        .block();
  }
}

@Log4j2
@Component
class KeycloakLogoutHandler extends SecurityContextLogoutHandler {
  private final OAuth2AuthorizedClientRepository clientRepository;

  public KeycloakLogoutHandler(OAuth2AuthorizedClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() instanceof OidcUser) {
      // since authentiaction will not have the info about access & refresh tokens, we need to fetch OAuth2AuthorizedClient
      OAuth2AuthorizedClient authorizedClient = clientRepository.loadAuthorizedClient(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(), authentication, request);
      super.logout(request, response, authentication);
      Object principal = authentication.getPrincipal();
      URL issuer = ((OidcUser) principal).getIssuer();
      // as per this https://github.com/keycloak/keycloak/blob/36b0d8b80e97de6db3e30ea9b79ef9db831d9b1c/services/src/main/java/org/keycloak/protocol/oidc/endpoints/LogoutEndpoint.java#L172
      // we have to call `<keycloack server>/auth/realms/<realm>/protocol/openid-connect/logout` with client basic auth & refresh_token as form param
      String logoutUri = UriComponentsBuilder.fromHttpUrl(issuer.toString()).pathSegment("/protocol/openid-connect/logout").build().toUriString();
      String authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString((authorizedClient.getClientRegistration().getClientId() + ":" + authorizedClient.getClientRegistration().getClientSecret()).getBytes());
      WebClient webClient = WebClient.builder().build();
      webClient
          .post()
          .uri(logoutUri)
          .headers(headers -> headers.add(AUTHORIZATION, authorizationHeaderValue))
          .body(fromFormData("refresh_token", requireNonNull(authorizedClient.getRefreshToken()).getTokenValue()))
          .exchange()
          .map(logoutResponse -> {
            boolean success = OK != logoutResponse.statusCode();
            if (!success) {
              log.warn("Error while logging out from remote IDP. Reason {}", logoutResponse.toEntity(String.class));
            }
            return success;
          })
          .block();
    } else {
      super.logout(request, response, authentication);
    }
  }
}

package com.acme.api.gateway.proxy;

import brave.Tracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.csrf.DefaultCsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.HYSTRIX_EXECUTION_EXCEPTION_ATTR;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME;

@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableCircuitBreaker
// TODO: There is a bug in Spring OAuth2 integration with Cloud gateway w.r.t refresh tokens not being automatically renewed by TokenRelayGatewayFilterFactory
public class ApiGatewayProxyApplication {
  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayProxyApplication.class, args);
  }
}

@RestController
class GatewatResource {
  @GetMapping(value = "/", produces = TEXT_PLAIN_VALUE)
  Mono<String> showSuggestions(@ModelAttribute("csrfToken") Mono<DefaultCsrfToken> csrfTokenMono, WebSession session) {
    //@formatter:off
    return csrfTokenMono.map(csrfToken -> {
      return "# Inventory for all product codes can be fetched using " + "\n"
              + "http -v :8080/order-service/inventories \"Cookie: SESSION=" + session.getId() + "\"" + "\n"
              + "# Inventory for a specific product can be fetched using " + "\n"
              + "http -v :8080/order-service/inventories/A \"Cookie: SESSION=" + session.getId() + "\"" + "\n"
              + "# Orders can be fetched using " + "\n"
              + "http -v :8080/order-service/orders \"Cookie: SESSION=" + session.getId() + "\"" + "\n"
              + "# A specific order can be fetched using " + "\n"
              + "http -v :8080/order-service/orders/1 \"Cookie: SESSION=" + session.getId() + "\"" + "\n"
              + "# Order can posted using " + "\n"
              + "http -vj POST :8080/order-service/orders productCode=A quantity=1 \"Cookie: SESSION=" + session.getId() + "\" \"" + csrfToken.getHeaderName() + ": " + csrfToken.getToken() + "\"\n"
              + "# Make direct calls to inventory service using" + "\n"
              + "export TOKEN=$(http --form --auth inventory-service-client:super-duper-secret --auth-type basic POST http://idp:9999/auth/realms/demo/protocol/openid-connect/token grant_type=client_credentials | jq '.access_token' | sed 's/\"//g')" + "\n"
              + "# Fetch all inventory item status using" + "\n"
              + "http -v :28080 \"Authorization: Bearer $TOKEN\"" + "\n"
              + "# Fetch a specific inventory item status using" + "\n"
              + "http -v :28080/A \"Authorization: Bearer $TOKEN\"" + "\n"
              + "# Can access actuator using" + "\n"
              + "http -vj :8080/actuator --auth actuator:password" + "\n"
              + "# Can update environment using" + "\n"
              + "http -vj POST :18080/actuator/env name=tunable.sleepTimeInMillis value=10000 --auth actuator:password" + "\n"
              + "# Can view hystrix from terminal using" + "\n"
              + "http -v --stream :8080/actuator/hystrix.stream --auth actuator:password" + "\n"
              + "# Can run simultaneous requests using" + "\n"
              + "seq 30 | parallel --gnu -n 0 \"http -v GET :8080/order-service/orders \\\"Cookie: SESSION=" + session.getId() + "\\\"\"";
    });
    //@formatter:on
  }

  @GetMapping("fallback")
  String fallback(ServerWebExchange exchange) {
    Throwable throwable = (Throwable) exchange.getAttribute(HYSTRIX_EXECUTION_EXCEPTION_ATTR);
    return "Returning fallback. Exception type " + throwable.getClass().getCanonicalName() + " ; Exception message is: "+ throwable.getMessage();
  }
}
//
//@Configuration
//class Test {
//  private final Tracer tracer;
//
//  public Test(Tracer tracer) {
//    this.tracer = tracer;
//  }
//}

package com.acme.api.gateway.proxy.config;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME;

@ControllerAdvice
public class CsrfControllerAdvice {
  @ModelAttribute
  public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
    Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
    if (csrfToken != null) { // for actuator flows, we explicitly disabled csrf. So, we dont expect csrf token for that flow
      return csrfToken.doOnSuccess(token -> exchange.getAttributes().put(DEFAULT_CSRF_ATTR_NAME, token));
    }
    return Mono.empty();
  }
}

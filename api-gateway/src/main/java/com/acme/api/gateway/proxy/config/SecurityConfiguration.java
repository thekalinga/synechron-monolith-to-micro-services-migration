package com.acme.api.gateway.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
  @Bean
  SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
    // @formatter:off
		http
			.authorizeExchange()
				.anyExchange().authenticated()
				.and()
			.oauth2Client().and()
      .oauth2Login().and()
			.httpBasic().disable()
			.formLogin().disable();
		// @formatter:on
    return http.build();
  }
}

package com.acme.api.gateway.proxy.config;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@Order(1)
public class ActuatorSecurityConfiguration {
  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.securityMatcher(EndpointRequest.toAnyEndpoint())
        .csrf().disable()
        .logout().disable()
        .httpBasic().and()
        .formLogin().disable()
        .authorizeExchange()
          .matchers(EndpointRequest.to("info", "health")).permitAll()
          .matchers(EndpointRequest.toAnyEndpoint()).hasRole("ACTUATOR");
    return http.build();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsRepository(PasswordEncoder passwordEncoder) {
    UserDetails actuatorUser = User.withUsername("actuator").roles("ACTUATOR").password(passwordEncoder.encode("password"))
        .build();
    return new MapReactiveUserDetailsService(actuatorUser);
  }
}

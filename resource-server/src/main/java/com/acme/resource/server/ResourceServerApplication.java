package com.acme.resource.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
public class ResourceServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(ResourceServerApplication.class, args);
  }
}

@EnableWebSecurity
class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .httpBasic().disable()
        .formLogin().disable()
        .authorizeRequests()
          .anyRequest().access("principal?.claims['email'] == 'user@acme.com'")
          .and()
        .oauth2ResourceServer()
          .jwt()
          .and()
          .and()
        .oauth2Login()
          .and()
        .oauth2Client()
          .authorizationCodeGrant();
  }
}

@RestController
class UserResource {
  @GetMapping
  Map<String, Object> user(@AuthenticationPrincipal Jwt jwt) {
    return jwt.getClaims();
  }
}

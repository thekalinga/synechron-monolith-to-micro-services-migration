package com.acme.micro.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.to;
import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable()
      .httpBasic().and()
      .formLogin().disable()
      .authorizeRequests()
        .requestMatchers(to("info", "health")).permitAll()
        .requestMatchers(toAnyEndpoint()).hasRole("ACTUATOR")
        .anyRequest().authenticated()
        .and()
      .oauth2ResourceServer() // for authenticating downstream requests from gateway
        .jwt();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    UserDetails actuatorUser = User.withUsername("actuator").roles("ACTUATOR")
        .password(passwordEncoder.encode("password")).build();
    auth.inMemoryAuthentication()
        .passwordEncoder(passwordEncoder)
        .withUser(actuatorUser);
  }
}

package com.acme.micro.order.config;

import brave.propagation.CurrentTraceContext;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommonConfiguration {
  @Bean
  @LoadBalanced
  RestTemplate loadBalancedRestTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}

package com.acme.api.gateway.proxy;

import brave.Tracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableZuulProxy
public class ApiGatewayProxyApplication {
  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayProxyApplication.class, args);
  }
}

@Configuration
class Test {
  private final Tracer tracer;

  public Test(Tracer tracer) {
    this.tracer = tracer;
  }
}

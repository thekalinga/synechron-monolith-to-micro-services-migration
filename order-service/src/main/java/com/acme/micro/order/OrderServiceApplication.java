package com.acme.micro.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableScheduling
public class OrderServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderServiceApplication.class);
  }
}


//@Configuration
//@Log4j2
//class ConfigEvaluator {
//  public ConfigEvaluator(@Value("${custom.property}") int value) {
//    log.debug("Retried the custom value of {}", value);
//  }

//  @Bean
//  @RefreshScope
//  CustomBean customBean() {
//    return CustomB
//  }
//}


//@ConfigurationProperties()
//class CustomBean {
//
//}

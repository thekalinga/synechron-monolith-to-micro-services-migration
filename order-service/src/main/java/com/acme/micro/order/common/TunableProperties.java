package com.acme.micro.order.common;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tunable")
@Data
@ToString
public class TunableProperties {
  private int sleepTimeInMillis;
}

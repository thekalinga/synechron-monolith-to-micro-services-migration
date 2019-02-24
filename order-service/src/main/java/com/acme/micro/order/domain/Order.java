package com.acme.micro.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Order {
  @Id
  @GeneratedValue
  private int id;
  @Nullable
  private String productCode;
  @Nullable
  private String productName;
  @Min(1)
  private int quantity;
  @Enumerated(STRING)
  private OrderStatus status;
  private int leaseId;
  private LocalDateTime leaseValidTill;
}

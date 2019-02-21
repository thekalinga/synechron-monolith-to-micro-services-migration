package com.acme.micro.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class InventoryItem {
  @Id
  @GeneratedValue
  private int id;
  @Version
  private int version;
  @NotNull
  private String productName;
  @NotNull
  private String productCode;
  @NotNull
  private int quantity;

  public void reduceQuantityBy(int quantity) {
    this.quantity -= quantity;
  }
}

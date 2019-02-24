package com.acme.micro.inventory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.mapping.Bag;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.util.Set;

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
  @OneToMany(mappedBy = "item")
  private Set<InventoryItemLease> leases;

  public void reduceQuantityBy(int quantity) {
    this.quantity -= quantity;
  }

  public void increaseQuantityBy(int quantity) {
    this.quantity += quantity;
  }
}

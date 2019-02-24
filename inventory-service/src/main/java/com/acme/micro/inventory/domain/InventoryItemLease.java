package com.acme.micro.inventory.domain;

import com.acme.common.order.inventory.LeaseConfirmationStatus;
import com.acme.micro.inventory.repository.LeaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
public class InventoryItemLease {
  @Id
  @GeneratedValue
  private int id;
  @Version
  private int version;
  @Min(1)
  @Column(unique = true) // we want to make sure repeated processing (in parallel/due to previous crash) of events does not result in duplicate reservations for the same order id
  private int orderId;
  @ManyToOne(optional = false)
  @JoinColumn
  private InventoryItem item;
  @Min(1)
  private int quantity;
  @NotNull
  private LocalDateTime validTill;
  @NotNull
  @Enumerated(STRING)
  private LeaseStatus status;
}

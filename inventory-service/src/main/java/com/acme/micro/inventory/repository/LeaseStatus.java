package com.acme.micro.inventory.repository;

import com.acme.common.order.inventory.LeaseConfirmationStatus;
import lombok.Getter;
import org.apache.commons.lang.NotImplementedException;

import static com.acme.common.order.inventory.LeaseConfirmationStatus.lease_confirmed;
import static com.acme.common.order.inventory.LeaseConfirmationStatus.lease_expired;

@Getter
public enum LeaseStatus {
  pending(false), confirmed(true), expired(true);

  private final boolean terminalState;

  LeaseStatus(boolean terminalState) {
    this.terminalState = terminalState;
  }

  public LeaseConfirmationStatus toConfirmationStatus() {
    switch (this) {
      case confirmed:
        return lease_confirmed;
      case expired:
        return lease_expired;
      default:
        throw new NotImplementedException("Mapping between " + this + " status <--> LeaseConfirmationStatus is intentionally not defined");
    }
  }
}

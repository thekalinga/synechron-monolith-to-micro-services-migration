package com.acme.micro.order.domain;

public enum OrderStatus {
  will_attempt_lease_acquire, lease_could_not_be_acquired, lease_acquired, confirmed, cancelled_thru_compensation
}

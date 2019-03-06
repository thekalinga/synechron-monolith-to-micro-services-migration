export enum OrderStatus {
    will_attempt_lease_acquire,
    lease_could_not_be_acquired,
    lease_acquired,
    confirmed,
    unexpected_error_while_confirming_lease,
    lease_expired, cancelled_thru_compensation
}

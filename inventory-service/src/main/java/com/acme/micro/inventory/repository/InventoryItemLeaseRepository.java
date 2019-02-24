package com.acme.micro.inventory.repository;

import com.acme.micro.inventory.domain.InventoryItemLease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryItemLeaseRepository extends JpaRepository<InventoryItemLease, Integer> {
  Optional<InventoryItemLease> findByOrderId(int orderId);

  List<InventoryItemLease> findAllByValidTillBeforeAndStatus(LocalDateTime now, LeaseStatus status);
}

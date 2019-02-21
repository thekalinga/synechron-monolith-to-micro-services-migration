package com.acme.monolith.repository;

import com.acme.monolith.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
  Optional<InventoryItem> findByProductCode(String code);
}

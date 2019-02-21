package com.acme.micro.inventory.repository;

import com.acme.micro.inventory.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
  Optional<InventoryItem> findByProductCode(String code);
}

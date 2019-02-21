package com.acme.monolith.repository;

import com.acme.monolith.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
  Optional<Product> findByCode(String productCode);
}

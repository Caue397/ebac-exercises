package dev.ms.product.repository;

import dev.ms.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByName(String name);
}

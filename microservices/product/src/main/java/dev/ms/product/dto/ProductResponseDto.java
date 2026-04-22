package dev.ms.product.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Timestamp createdAt,
        Timestamp updatedAt
) {}

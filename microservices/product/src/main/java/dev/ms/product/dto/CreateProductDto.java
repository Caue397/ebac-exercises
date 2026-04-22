package dev.ms.product.dto;

import java.math.BigDecimal;

public record CreateProductDto(
        String name,
        String description,
        BigDecimal price,
        Integer stock
) {}

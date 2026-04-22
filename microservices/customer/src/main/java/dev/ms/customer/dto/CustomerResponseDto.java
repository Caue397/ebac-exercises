package dev.ms.customer.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record CustomerResponseDto(
        UUID id,
        String name,
        String email,
        String cpf,
        Timestamp createdAt,
        Timestamp updatedAt
) {}

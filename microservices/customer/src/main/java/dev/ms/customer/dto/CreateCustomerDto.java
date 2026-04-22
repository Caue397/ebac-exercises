package dev.ms.customer.dto;

public record CreateCustomerDto(
        String name,
        String email,
        String cpf
) {}

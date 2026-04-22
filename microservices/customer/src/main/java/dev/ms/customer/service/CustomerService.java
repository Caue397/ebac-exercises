package dev.ms.customer.service;

import dev.ms.customer.domain.Customer;
import dev.ms.customer.dto.CreateCustomerDto;
import dev.ms.customer.dto.CustomerResponseDto;
import dev.ms.customer.dto.UpdateCustomerDto;
import dev.ms.customer.repository.ICustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final ICustomerRepository customerRepository;

    public CustomerResponseDto getById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return toResponse(customer);
    }

    public List<CustomerResponseDto> getAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponseDto create(CreateCustomerDto dto) {
        customerRepository.findByCpf(dto.cpf()).ifPresent(c -> {
            throw new RuntimeException("Customer already exists with CPF: " + dto.cpf());
        });
        customerRepository.findByEmail(dto.email()).ifPresent(c -> {
            throw new RuntimeException("Customer already exists with e-mail: " + dto.email());
        });

        Customer customer = Customer.builder()
                .name(dto.name())
                .email(dto.email())
                .cpf(dto.cpf())
                .build();

        return toResponse(customerRepository.save(customer));
    }

    public CustomerResponseDto update(UUID id, UpdateCustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        if (dto.name() != null) customer.setName(dto.name());
        if (dto.email() != null) customer.setEmail(dto.email());

        return toResponse(customerRepository.save(customer));
    }

    public void delete(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponseDto toResponse(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getCpf(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}

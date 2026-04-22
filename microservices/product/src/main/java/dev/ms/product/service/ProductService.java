package dev.ms.product.service;

import dev.ms.product.domain.Product;
import dev.ms.product.dto.CreateProductDto;
import dev.ms.product.dto.ProductResponseDto;
import dev.ms.product.dto.UpdateProductDto;
import dev.ms.product.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final IProductRepository productRepository;

    public ProductResponseDto getById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return toResponse(product);
    }

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponseDto create(CreateProductDto dto) {
        productRepository.findByName(dto.name()).ifPresent(p -> {
            throw new RuntimeException("Product already exists with name: " + dto.name());
        });

        Product product = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .stock(dto.stock())
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponseDto update(UUID id, UpdateProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (dto.name() != null) product.setName(dto.name());
        if (dto.description() != null) product.setDescription(dto.description());
        if (dto.price() != null) product.setPrice(dto.price());
        if (dto.stock() != null) product.setStock(dto.stock());

        return toResponse(productRepository.save(product));
    }

    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponseDto toResponse(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}

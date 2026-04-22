package dev.ms.product.integration;

import dev.ms.product.dto.CreateProductDto;
import dev.ms.product.dto.ProductResponseDto;
import dev.ms.product.dto.UpdateProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductIntegrationTests {

    @LocalServerPort
    private int port;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldCreateProduct() {
        client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateProductDto("Notebook", "Notebook gamer", new BigDecimal("4500.00"), 10))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isEqualTo("Notebook");
                    assertThat(response.description()).isEqualTo("Notebook gamer");
                    assertThat(response.price()).isEqualByComparingTo(new BigDecimal("4500.00"));
                    assertThat(response.stock()).isEqualTo(10);
                });
    }

    @Test
    void shouldGetProductById() {
        // cria o product primeiro
        ProductResponseDto created = client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateProductDto("Mouse", "Mouse sem fio", new BigDecimal("150.00"), 50))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        // busca pelo id
        client.get().uri("/products/{id}", created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(created.id());
                    assertThat(response.name()).isEqualTo("Mouse");
                });
    }

    @Test
    void shouldGetAllProducts() {
        client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateProductDto("Teclado", "Teclado mecânico", new BigDecimal("350.00"), 20))
                .exchange()
                .expectStatus().isCreated();

        client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateProductDto("Monitor", "Monitor 27 polegadas", new BigDecimal("1800.00"), 15))
                .exchange()
                .expectStatus().isCreated();

        client.get().uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void shouldDeleteProduct() {
        ProductResponseDto created = client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateProductDto("Webcam", "Webcam HD", new BigDecimal("250.00"), 30))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        client.delete().uri("/products/{id}", created.id())
                .exchange()
                .expectStatus().isNoContent();

        client.get().uri("/products/{id}", created.id())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldUpdateProduct() {
        ProductResponseDto created = client.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateProductDto("Headset", "Headset gamer", new BigDecimal("400.00"), 25))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponseDto.class)
                .returnResult()
                .getResponseBody();

        client.put().uri("/products/{id}", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UpdateProductDto("Headset Pro", "Headset gamer com microfone", new BigDecimal("550.00"), 40))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(created.id());
                    assertThat(response.name()).isEqualTo("Headset Pro");
                    assertThat(response.description()).isEqualTo("Headset gamer com microfone");
                    assertThat(response.price()).isEqualByComparingTo(new BigDecimal("550.00"));
                    assertThat(response.stock()).isEqualTo(40);
                });
    }
}

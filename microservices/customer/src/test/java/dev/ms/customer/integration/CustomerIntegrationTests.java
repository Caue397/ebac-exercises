package dev.ms.customer.integration;

import dev.ms.customer.dto.CreateCustomerDto;
import dev.ms.customer.dto.CustomerResponseDto;
import dev.ms.customer.dto.UpdateCustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerIntegrationTests {

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
    void shouldCreateCustomer() {
        client.post().uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateCustomerDto("João", "joao@email.com", "12345678900"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isEqualTo("João");
                    assertThat(response.email()).isEqualTo("joao@email.com");
                    assertThat(response.cpf()).isEqualTo("12345678900");
                });
    }

    @Test
    void shouldGetCustomerById() {
        // cria o customer primeiro
        CustomerResponseDto created = client.post().uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateCustomerDto("Maria", "maria@email.com", "98765432100"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseDto.class)
                .returnResult()
                .getResponseBody();

        // busca pelo id
        client.get().uri("/customers/{id}", created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(created.id());
                    assertThat(response.name()).isEqualTo("Maria");
                });
    }

    @Test
    void shouldGetAllCustomers() {
        client.post().uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateCustomerDto("Ana", "ana@email.com", "11111111111"))
                .exchange()
                .expectStatus().isCreated();

        client.post().uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateCustomerDto("Pedro", "pedro@email.com", "22222222222"))
                .exchange()
                .expectStatus().isCreated();

        client.get().uri("/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void shouldDeleteCustomer() {
        CustomerResponseDto created = client.post().uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateCustomerDto("Carlos", "carlos@email.com", "33333333333"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseDto.class)
                .returnResult()
                .getResponseBody();

        client.delete().uri("/customers/{id}", created.id())
                .exchange()
                .expectStatus().isNoContent();

        client.get().uri("/customers/{id}", created.id())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldUpdateCustomer() {
        CustomerResponseDto created = client.post().uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateCustomerDto("Lucas", "lucas@email.com", "44444444444"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseDto.class)
                .returnResult()
                .getResponseBody();

        client.put().uri("/customers/{id}", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UpdateCustomerDto("Lucas Silva", "lucas.silva@email.com"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(created.id());
                    assertThat(response.name()).isEqualTo("Lucas Silva");
                    assertThat(response.email()).isEqualTo("lucas.silva@email.com");
                    assertThat(response.cpf()).isEqualTo("44444444444");
                });
    }
}

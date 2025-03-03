package com.brand.practica_banco.Controller;

import com.brand.practica_banco.Entity.Cuenta;
import com.brand.practica_banco.Entity.CuentaDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import javax.print.attribute.standard.Media;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private ObjectMapper objectMapper;

    private String getBaseUrl(){
        return "http://localhost:" + port + "/api/banco/cuenta";
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    void listar() {
        ResponseEntity<Cuenta[]> response = restTemplate.getForEntity(getBaseUrl(), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(cuentas).isNotEmpty();
        assertThat(cuentas).hasSize(2);
        assertThat(cuentas.get(0).getId()).isEqualTo(1L);
        assertThat(cuentas.get(0).getNombre()).isEqualTo("Andres");
        assertThat(cuentas.get(0).getSaldo()).isEqualTo("10000.00");
        assertThat(cuentas.get(1).getId()).isEqualTo(2L);
        assertThat(cuentas.get(1).getNombre()).isEqualTo("John");
        assertThat(cuentas.get(1).getSaldo()).isEqualTo("20000.00");

    }

    @Order(2)
    @Test
    void guardar() {
        CuentaDto cuentaDto = new CuentaDto(null, "Juan Perez", new BigDecimal("20000.00"));

        ResponseEntity<Cuenta> response = restTemplate.postForEntity(getBaseUrl(), cuentaDto, Cuenta.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Cuenta cuentaGuardada = response.getBody();
        assertThat(cuentaGuardada).isNotNull();
        assertThat(cuentaGuardada.getId()).isEqualTo(3L);
        assertThat(cuentaGuardada.getNombre()).isEqualTo("Juan Perez");
        assertThat(cuentaGuardada.getSaldo()).isEqualTo("20000.00");
    }

    @Order(3)
    @Test
    void editar() {
        Long id = 3L;
        CuentaDto cuentaDto = new CuentaDto(null, "Uriel", new BigDecimal("15000.00"));
        HttpEntity<CuentaDto> requestEntity = new HttpEntity<>(cuentaDto);
        ResponseEntity<CuentaDto> response = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.PUT, requestEntity, CuentaDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        CuentaDto updatedResponse = response.getBody();
        assertThat(updatedResponse).isNotNull();
        assertThat(updatedResponse.getId()).isEqualTo(3L);
        assertThat(updatedResponse.getNombre()).isEqualTo("Uriel");
        assertThat(updatedResponse.getSaldo()).isEqualTo("15000.00");
    }

    @Order(4)
    @Test
    void buscarPorId() {
        Long id = 3L;

        ResponseEntity<Cuenta> response = restTemplate.getForEntity(getBaseUrl() + "/" + id, Cuenta.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Cuenta cuentaFound = response.getBody();
        assertThat(cuentaFound).isNotNull();
        assertThat(cuentaFound.getId()).isEqualTo(3L);
        assertThat(cuentaFound.getNombre()).isEqualTo("Uriel");
        assertThat(cuentaFound.getSaldo()).isEqualTo("15000.00");
    }

    @Order(5)
    @Test
    void eliminar() {
        Long id = 3L;

        ResponseEntity<Void> exchange = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.DELETE, null, Void.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(exchange.hasBody()).isFalse();
    }

    @Order(6)
    @Test
    void buscarPorNombre() {
        String nombre = "Andres";

        ResponseEntity<Cuenta> response = restTemplate.getForEntity(getBaseUrl() + "/nombre/" + nombre, Cuenta.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Cuenta responseBody = response.getBody();
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getNombre()).isEqualTo("Andres");
        assertThat(responseBody.getSaldo()).isEqualTo("10000.00");
    }
}
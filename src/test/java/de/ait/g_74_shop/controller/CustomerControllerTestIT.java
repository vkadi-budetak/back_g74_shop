package de.ait.g_74_shop.controller;

import de.ait.g_74_shop.domain.Customer;
import de.ait.g_74_shop.dto.customer.CustomerDto;
import de.ait.g_74_shop.dto.customer.CustomerSaveDto;
import de.ait.g_74_shop.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private CustomerRepository repository;

    private static final String CUSTOMERS_RESOURCE = "/customers";

    // Пишемо перший тест - збереження даних в базі
    @Test
    public void shouldSaveCustomer() {
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName("Test name");

        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        ResponseEntity<CustomerDto> response = httpClient.postForEntity(CUSTOMERS_RESOURCE, request, CustomerDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        CustomerDto dto = response.getBody();
        assertNotNull(dto, "Response body shouldn't be null");
        assertNotNull(dto.getId(), "Response body shouldn't be null");
        assertEquals((saveDto.getName()), dto.getName(), "Returned customer has incorrect name");

        // Перевіряємо чи дійсно клієнт збережений в базу даних
        Customer savedCustomer = repository.findByIdAndActiveTrue(dto.getId()).orElse(null);
        assertNotNull(savedCustomer, "Customer wasn't properly saved to the database");
        assertEquals(saveDto.getName(), dto.getName(), "Saved customer has incorrect name");
    }

    /// ПИШЕМО НЕГАТИВНИЙ ТЕСТ(і будемо відправляти некоректні дані)
    @Test
    public void shouldReturn400WhenNameIsEmpty() {
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName("");

        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        ResponseEntity<String> response = httpClient.postForEntity(CUSTOMERS_RESOURCE, request, String.class);

        assertEquals((HttpStatus.BAD_REQUEST), response.getStatusCode(), "Response has unexpected status");

        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("name"), "Response body doesn't contain expected message");
    }

    @BeforeEach
    public void startUp(){
        Customer activeTestCustomer = new Customer();
        activeTestCustomer.setName("Active name");
        activeTestCustomer.setActive(true);

        Customer inactiveTestCustomer = new Customer();
        inactiveTestCustomer.setName("Inactive name");
        inactiveTestCustomer.setActive(false);

        repository.saveAll(List.of(activeTestCustomer, inactiveTestCustomer));
    }

    @AfterEach
    public void cleanDatabase() {
        repository.deleteAll();
    }
}
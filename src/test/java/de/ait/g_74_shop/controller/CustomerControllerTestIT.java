package de.ait.g_74_shop.controller;

import de.ait.g_74_shop.domain.Cart;
import de.ait.g_74_shop.domain.Customer;
import de.ait.g_74_shop.dto.cart.CartDto;
import de.ait.g_74_shop.dto.customer.CustomerDto;
import de.ait.g_74_shop.dto.customer.CustomerSaveDto;
import de.ait.g_74_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_74_shop.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient;

    @Autowired
    private CustomerRepository repository;

    private static final String CUSTOMERS_RESOURCE = "/customers";

    private static final String TEST_CUSTOMER_NAME = "Test name";

    // Пишемо перший тест - збереження даних в базі
    @Test
    public void shouldSaveCustomer() {
        CustomerSaveDto saveDto = new CustomerSaveDto();
        saveDto.setName(TEST_CUSTOMER_NAME);

        HttpEntity<CustomerSaveDto> request = new HttpEntity<>(saveDto);

        ResponseEntity<CustomerDto> response = httpClient.postForEntity(CUSTOMERS_RESOURCE, request, CustomerDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        CustomerDto customerDto = response.getBody();
        assertNotNull(customerDto, "Response body shouldn't be null");
        assertNotNull(customerDto.getId(), "Response body shouldn't be null");
        assertEquals((saveDto.getName()), customerDto.getName(), "Returned customer has incorrect name");

        CartDto cartDto = customerDto.getCart();
        assertNotNull(cartDto, "Customer cart shouldn't be null");
        assertNotNull(cartDto.getPositions(), "Cart positions list shouldn't be null");
        assertTrue(cartDto.getPositions().isEmpty(), "Cart positions list should be empty");

        // Перевіряємо чи дійсно клієнт збережений в базу даних
        Customer savedCustomer = repository.findByIdAndActiveTrue(customerDto.getId()).orElse(null);
        assertNotNull(savedCustomer, "Customer wasn't properly saved to the database");
        assertEquals(saveDto.getName(), customerDto.getName(), "Saved customer has incorrect name");

        Cart cart = savedCustomer.getCart();
        assertNotNull(cart, "Customer cart shouldn't be null");
        assertNotNull(cart.getId(), "Customer cart id shouldn't be null");
        assertNotNull(cart.getPositions(), "Cart positions list shouldn't be null");
        assertTrue(cart.getPositions().isEmpty(), "Cart positions list should be empty");
    }

    /// ПИШЕМО НЕГАТИВНИЙ ТЕСТ(і будемо відправляти некоректні дані)
    // Перевірка обробки "неактивних" сутностей та помилки 404.
    @Test
    public void shouldReturn400WhenNameIsEmpty() {
        Customer customer = new Customer();
        customer.setName(TEST_CUSTOMER_NAME);
        customer.setActive(false);

        Cart cart = new Cart();
        cart.setCustomer(customer);
        customer.setCart(cart);

        repository.save(customer);

        String url = CUSTOMERS_RESOURCE + "/" + customer.getId();

        ResponseEntity<String> response = httpClient.getForEntity(url, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("not found"), "Response body doesn't contain expected message");
    }

    // Перевірка фільтрації списку.
    @Test
    public void shouldReturnOnlyActiveCustomers() {
        addCustomersToDB();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<CustomerDto>> response = httpClient.exchange(
                CUSTOMERS_RESOURCE, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<CustomerDto> customers = response.getBody();
        assertNotNull(customers, "Response body shouldn't be null");
        assertEquals(2, customers.size(), "Returned list has unexpected size");
        assertTrue(customers.stream().anyMatch(x -> x.getName().equals(TEST_CUSTOMER_NAME + "a")),
                "Returned list doesn't have expected customer");
        assertTrue(customers.stream().anyMatch(x -> x.getName().equals(TEST_CUSTOMER_NAME + "b")),
                "Returned list doesn't have expected customer");
    }

    // Перевірка валідації при оновленні.
    @Test
    public void shouldReturn400IfCustomerNewNameIsEmpty() {
        Long id = addActiveCustomerToDBAndGetId();

        CustomerUpdateDto updateDto = new CustomerUpdateDto();
        updateDto.setNewName("");
        HttpEntity<CustomerUpdateDto> request = new HttpEntity<>(updateDto);
        String url = CUSTOMERS_RESOURCE + "/" + id;

        ResponseEntity<String> response = httpClient.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("title"), "Response body doesn't contain expected message");
    }

    // Перевірка "м'якого видалення"
    @Test
    public void shouldMarkActiveCustomerAsInactive() {
        Long id = addActiveCustomerToDBAndGetId();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = CUSTOMERS_RESOURCE + "/" + id;

        ResponseEntity<Void> response = httpClient.exchange(url, HttpMethod.DELETE, request, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        Customer customer = repository.findById(id).orElse(null);

        assertNotNull(customer, "Customer was deleted from DB instead of marking him as inactive");
        assertFalse(customer.isActive(), "Customer wasn't marked as inactive");
    }

    private Long addActiveCustomerToDBAndGetId() {
        Customer customer = new Customer();
        customer.setName(TEST_CUSTOMER_NAME);
        customer.setActive(true);

        Cart cart = new Cart();
        cart.setCustomer(customer);
        customer.setCart(cart);

        repository.save(customer);

        return customer.getId();
    }

    // Він заповнює базу даних тестовими даними
    private void addCustomersToDB() {
        Customer customer1 = new Customer();
        customer1.setName(TEST_CUSTOMER_NAME + "a");
        customer1.setActive(true);

        Cart cart1 = new Cart();
        cart1.setCustomer(customer1);
        customer1.setCart(cart1);

        Customer customer2 = new Customer();
        customer2.setName(TEST_CUSTOMER_NAME + "b");
        customer2.setActive(true);

        Cart cart2 = new Cart();
        cart2.setCustomer(customer2);
        customer2.setCart(cart2);

        Customer customer3 = new Customer();
        customer3.setName(TEST_CUSTOMER_NAME + "c");

        Cart cart3 = new Cart();
        cart3.setCustomer(customer3);
        customer3.setCart(cart3);

        repository.saveAll(List.of(customer1, customer2, customer3));
    }

    @AfterEach
    public void cleanDatabase() {
        repository.deleteAll();
    }
}
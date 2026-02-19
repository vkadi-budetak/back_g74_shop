package de.ait.g_74_shop.controller;

import de.ait.g_74_shop.domain.Product;
import de.ait.g_74_shop.dto.product.ProductDto;
import de.ait.g_74_shop.dto.product.ProductSaveDto;
import de.ait.g_74_shop.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest - анотація із налаштуванням, щоб запускався на рандомному порті
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTestIT {

    @Autowired
    private TestRestTemplate httpClient; // Об'єкт вміє відправляти http запроси автоматично

    /// репозиторій продукта
    @Autowired
    private ProductRepository repository;


    // Повторяющие значения в разных методах рекомендуется выносить в константы
    private static final String PRODUCTS_RESOURCE = "/products";

    // Пишемо перший тест - збереження даних в базі
    @Test
    public void shouldSaveProduct() {
        /// Создаем ДТО для отправки в теле запроса
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("Test title");
        saveDto.setPrice(new BigDecimal("777.00"));

        /// Создаем сам запрос вкладывая в него тело(ДТО)
        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto);

        ///отправляем запрос и получаем ответ
        ResponseEntity<ProductDto> response = httpClient.postForEntity(PRODUCTS_RESOURCE, request, ProductDto.class);

        /// Проверяем правильный ли пришел статус ответа
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Response has unexpected status");

        /// проверяем правильный ли ДТО пришел в ответ
        ProductDto dto = response.getBody();
        // проверяем ли непустое тело
        assertNotNull(dto, "Response body shouldn't be null");
        // присвоен ли id (не пустое)
        assertNotNull(dto.getId(), "Returned product id shouldn't be null");
        assertEquals(saveDto.getTitle(), dto.getTitle(), "Returned product has incorrect title");
        assertEquals(saveDto.getPrice(), dto.getPrice(), "Returned product has incorrect price");

        // Проверяем правильно ли продукт сохранен в базу данных
        Product savedProduct = repository.findByIdAndActiveTrue(dto.getId()).orElse(null);
        assertNotNull(savedProduct, "Product wasn't properly saved to the database");
        assertEquals(saveDto.getTitle(), dto.getTitle(), "Saved product has incorrect title");
        assertEquals(saveDto.getPrice(), dto.getPrice(), "Saved product has incorrect price");
    }


    /// ПИШЕМО НЕГАТИВНИЙ ТЕСТ(і будемо відправляти некоректні дані)
    @Test
    public void shouldReturn400WhenTitleIsEmpty() {
        ProductSaveDto saveDto = new ProductSaveDto();
        saveDto.setTitle("");
        saveDto.setPrice(new BigDecimal("777.00"));

        HttpEntity<ProductSaveDto> request = new HttpEntity<>(saveDto);

        ResponseEntity<String> response = httpClient.postForEntity(PRODUCTS_RESOURCE, request, String.class);

        assertEquals((HttpStatus.BAD_REQUEST), response.getStatusCode(), "Response has unexpected status");

        String body = response.getBody();
        assertNotNull(body, "Response body shouldn't be null");
        assertTrue(body.contains("title"), "Response body doesn't contain expected message");

    }

    // Метод для заполнения БД тестовым обьектом перед каждым тестом
    @BeforeEach
    public void startUp(){
        Product activeTestProduct = new Product();
        activeTestProduct.setTitle("Active product");
        activeTestProduct.setPrice(new BigDecimal("111.00"));
        activeTestProduct.setActive(true);

        Product inactiveTestProduct = new Product();
        inactiveTestProduct.setTitle("Inactive product");
        inactiveTestProduct.setPrice(new BigDecimal("222.00"));
        inactiveTestProduct.setActive(false);

        repository.saveAll(List.of(activeTestProduct, inactiveTestProduct));
    }

    // чистимо базу даних після кожного тесту (AfterEach - анотація - говорить запускатися після кожного тесту)
    @AfterEach
    public void cleanDatabase() {
        repository.deleteAll();
    }

}
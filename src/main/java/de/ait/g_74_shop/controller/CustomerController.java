package de.ait.g_74_shop.controller;


import de.ait.g_74_shop.domain.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    // тут буде поле із сервісом клієнтів щоб контролер
    // міг звертатися до серверу і визивати його методи

    /// Сохранить покупателя в базе данных.
    /// POST -> http://10.20.30.40:8080/customers ожидаем тело запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer save(@RequestBody Customer customer) {

        // Здесь будет вызов сервиса и метода для сохранения в БД продукта
        System.out.println("На вход пришел клиент: " + customer);
        return null;
    }

    /// Вернуть всех покупателей из базы данных.
    /// GET -> http://10.20.30.40:8080/customers
    @GetMapping
    public List<Customer> getAll() {
        System.out.println("Запрошены все клиенты");
        return null;
    }

    /// Вернуть одного покупателя из базы данных по его идентификатору.
    /// GET -> http://10.20.30.40:8080/customers/5
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {

        System.out.println("Запрошен клиент с id " + id);
        return null;
    }

    /// Изменить одного покупателя в базе данных по его идентификатору.
    /// PUT -> http://10.20.30.40:8080/customers/5
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Customer customer) {

        System.out.println("Пришел запрос на изменения клиента с id " + id);
        System.out.println("Новый клиент - " + customer.getName());
    }

    /// Удалить покупателя из базы данных по его идентификатору.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        System.out.println("Удален клиент с id " + id);
    }

    /// Восстановить удалённого покупателя в базе данных по его идентификатору.
    /// PUT -> http://10.20.30.40:8080/customers/5/restore
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        System.out.println("Восстановлен клиент с id " + id);
    }

    /// Вернуть общее количество покупателей в базе данных.
    /// GET -> http://10.20.30.40:8080/customers/count
    @GetMapping("/count")
    public int getCustomerQuantity() {
        System.out.println("Запрошено общее количество клиентов");
        return 0;
    }

    /// Вернуть стоимость корзины покупателя по его идентификатору.
    /// GET -> http://10.20.30.40:8080/customers/5/total-price
    @GetMapping("/{id}/total-price")
    public BigDecimal getCustomerTotalCost(@PathVariable Long id) {
        System.out.println("Запрошена стоимость корзины клиента id " + id);
        return null;
    }

    /// Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    /// GET -> http://10.20.30.40:8080/customers/3/average-price
    @GetMapping("/{id}/average-price")
    public BigDecimal getCustomerAveragePrice(@PathVariable Long id) {
        System.out.println("Запрошена средняя цена товаров в корзине клиента id " + id);
        return BigDecimal.ZERO;
    }

    /// Добавить товар в корзину покупателя по их идентификаторам.(по ID покупателя и ID товара)
    /// POST -> http://10.20.30.40:8080/customers/3/product/5 (../id_покупця/products/id_товару)
    @PostMapping("/{customerId}/products/{productId}")
    public void addProductToCart(@PathVariable Long customerId, @PathVariable Long productId) {
        System.out.println("Добавляем товар id " + productId + " клиенту id " + customerId);
    }

    /// Удалить товар из корзины покупателя по их идентификаторам.
    @DeleteMapping("/{customerId}/products/{productId}")
    public void removeProductFromCart(@PathVariable Long customerId, @PathVariable Long productId) {
        System.out.println("Удаляем товар id " + productId + " из корзины клиента id " + customerId);
    }

    /// Полностью очистить корзину покупателя по его идентификатору.
    @DeleteMapping("/{id}/cart")
    public void clearCart(@PathVariable Long id) {
        System.out.println("Очистка корзины для клиента id " + id);
    }


}

package de.ait.g_74_shop.controller;


import de.ait.g_74_shop.domain.Customer;
import de.ait.g_74_shop.dto.customer.CustomerDto;
import de.ait.g_74_shop.dto.customer.CustomerSaveDto;
import de.ait.g_74_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_74_shop.dto.position.PositionUpdateDto;
import de.ait.g_74_shop.service.interfaces.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    /// Сохранить покупателя в базе данных.
    /// POST -> http://10.20.30.40:8080/customers ожидаем тело запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto save(@RequestBody CustomerSaveDto saveDto) {

        // Здесь будет вызов сервиса и метода для сохранения в БД продукта
//        System.out.println("На вход пришел клиент: " + customer);

        return service.save(saveDto);
    }

    /// Вернуть всех покупателей из базы данных.
    /// GET -> http://10.20.30.40:8080/customers
    @GetMapping
    public List<CustomerDto> getAll() {
//        System.out.println("Запрошены все клиенты");
        return service.getAllActiveCustomers();
    }

    /// Вернуть одного покупателя из базы данных по его идентификатору.
    /// GET -> http://10.20.30.40:8080/customers/5
    @GetMapping("/{id}")
    public CustomerDto getById(@PathVariable Long id) {

//        System.out.println("Запрошен клиент с id " + id);
        return service.getActiveCustomerById(id);
    }

    /// Изменить одного покупателя в базе данных по его идентификатору.
    /// PUT -> http://10.20.30.40:8080/customers/5
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody CustomerUpdateDto updateDto) {

//        System.out.println("Пришел запрос на изменения клиента с id " + id);
//        System.out.println("Новый клиент - " + customer.getName());

        service.update(id, updateDto);
    }

    /// Удалить покупателя из базы данных по его идентификатору.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
//        System.out.println("Удален клиент с id " + id);
        service.deleteById(id);
    }

    /// Восстановить удалённого покупателя в базе данных по его идентификатору.
    /// PUT -> http://10.20.30.40:8080/customers/5/restore
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
//        System.out.println("Восстановлен клиент с id " + id);
        service.restoreById(id);
    }

    /// Вернуть общее количество покупателей в базе данных.
    /// GET -> http://10.20.30.40:8080/customers/count
    @GetMapping("/count")
    public int getCustomerQuantity() {
//        System.out.println("Запрошено общее количество клиентов");
        return (int) service.getAllActiveCustomersCount();
    }

    /// Вернуть стоимость корзины покупателя по его идентификатору.
    /// GET -> http://10.20.30.40:8080/customers/5/total-price
    @GetMapping("/{id}/total-price")
    public BigDecimal getCustomerTotalCost(@PathVariable Long id) {
//        System.out.println("Запрошена стоимость корзины клиента id " + id);
        return service.getCustomerCartTotalCost(id);
    }

    /// Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    /// GET -> http://10.20.30.40:8080/customers/3/average-price
    @GetMapping("/{id}/average-price")
    public BigDecimal getCustomerAveragePrice(@PathVariable Long id) {
//        System.out.println("Запрошена средняя цена товаров в корзине клиента id " + id);
//        return BigDecimal.ZERO;
        return service.getCustomerProductsAveragePrice(id);
    }

    /// Добавить товар в корзину покупателя по их идентификаторам.(по ID покупателя и ID товара)
    /// POST -> http://10.20.30.40:8080/customers/cart/3/product/5 (../id_покупця/cart/products/id_товару)
    @PostMapping("/{customerId}/cart/products/{productId}")
    public void addProductToCustomerCart(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @RequestBody PositionUpdateDto positionUpdateDto
    ) {
        service.addProductToCustomerCart(customerId, productId, positionUpdateDto);
    }

    /// Удалить товар из корзины покупателя по их идентификаторам.
    @PutMapping("/{customerId}/cart/products/{productId}/remove")
    public void removeProductFromCustomerCart(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @RequestBody PositionUpdateDto positionUpdateDto
    ) {
        service.removeProductFromCustomerCart(customerId, productId, positionUpdateDto);
    }

    /// Полностью очистить корзину покупателя по его идентификатору.
    @DeleteMapping("/{id}/cart")
    public void clearCustomerCart(@PathVariable Long id) {
        service.clearCustomerCart(id);
    }


}

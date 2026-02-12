package de.ait.g_74_shop.service.interfaces;

import de.ait.g_74_shop.domain.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    //    Сохранить покупателя в базе данных.
    Customer saveCustomer(Customer customer);

    //    Вернуть всех покупателей из базы данных.
    List<Customer> getAllCustomers();

    //    Вернуть одного покупателя из базы данных по его идентификатору.
    Customer getActiveCustomerById(Long id);

    //    Изменить одного покупателя в базе данных по его идентификатору.
    void updateCustomer(Long id, Customer customer);

    //    Удалить покупателя из базы данных по его идентификатору.
    void deleteCustomerById(Long id);

    //    Восстановить удалённого покупателя в базе данных по его идентификатору.
    void restoreCustomerById(Long id);

    //    Вернуть общее количество покупателей в базе данных.
    long getAllActiveCustomersCount();

    //    Вернуть стоимость корзины покупателя по его идентификатору.
    BigDecimal getAllActiveCustomersTotalCost(Long id);

    //    Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    BigDecimal getAllActiveCustomersAveragePrice(Long id);

    //    Добавить товар в корзину покупателя по их идентификаторам.
    void addProductToBasketById(Long customerId, long productId, Integer quantity);

    //    Удалить товар из корзины покупателя по их идентификаторам.
    void removeProductToBasketById(Long customerId, Long productId, Integer quantity);

    //    Полностью очистить корзину покупателя по его идентификатору.
    void clearBasket(Long customerId);
}

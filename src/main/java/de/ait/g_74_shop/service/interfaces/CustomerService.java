package de.ait.g_74_shop.service.interfaces;

import de.ait.g_74_shop.dto.customer.CustomerDto;
import de.ait.g_74_shop.dto.customer.CustomerSaveDto;
import de.ait.g_74_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_74_shop.dto.position.PositionUpdateDto;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    //    Сохранить покупателя в базе данных.
    CustomerDto save(CustomerSaveDto customer);

    //    Вернуть всех покупателей из базы данных.
    List<CustomerDto> getAllActiveCustomers();

    //    Вернуть одного покупателя из базы данных по его идентификатору.
    CustomerDto getActiveCustomerById(Long id);

    //    Изменить одного покупателя в базе данных по его идентификатору.
    void update(Long id, CustomerUpdateDto customer);

    //    Удалить покупателя из базы данных по его идентификатору.
    void deleteById(Long id);

    //    Восстановить удалённого покупателя в базе данных по его идентификатору.
    void restoreById(Long id);

    //    Вернуть общее количество покупателей в базе данных.
    long getAllActiveCustomersCount();

    //    Вернуть стоимость корзины покупателя по его идентификатору.
    BigDecimal getCustomerCartTotalCost(Long id);

    //    Вернуть среднюю стоимость продукта в корзине покупателя по его идентификатору.
    BigDecimal getCustomerProductsAveragePrice(Long id);

    //    Добавить товар в корзину покупателя по их идентификаторам.
    void addProductToCustomerCart(Long customerId, Long productId, PositionUpdateDto positionUpdateDto);

    //    Удалить товар из корзины покупателя по их идентификаторам.
    void removeProductFromCustomerCart(Long customerId, Long productId, PositionUpdateDto positionUpdateDto);

    //    Полностью очистить корзину покупателя по его идентификатору.
    void clearCustomerCart(Long id);
}

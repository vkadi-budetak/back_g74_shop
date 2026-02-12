package de.ait.g_74_shop.service;

import de.ait.g_74_shop.domain.Customer;
import de.ait.g_74_shop.domain.Position;
import de.ait.g_74_shop.repository.CustomerRepository;
import de.ait.g_74_shop.service.interfaces.CustomerService;
import de.ait.g_74_shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final ProductService service;


    public CustomerServiceImpl(CustomerRepository repository, ProductService service) {
        this.repository = repository;
        this.service = service;
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        customer.setActive(true);
        return repository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return repository.findAllByActiveTrue();
    }

    @Override
    public Customer getActiveCustomerById(Long id) {
        return repository.findByIdAndActiveTrue(id).orElse(null);
    }

    @Override
    @Transactional
    public void updateCustomer(Long id, Customer customer) {
        repository.findById(id)
                .ifPresent(x -> x.setName(customer.getName()));
    }

    @Override
    @Transactional
    public void deleteCustomerById(Long id) {
        Customer customer = getActiveCustomerById(id);
        if (customer == null) {
            return;
        }
        customer.setActive(false);
    }

    @Override
    public void restoreCustomerById(Long id) {
        repository.findById(id).ifPresent(x -> x.setActive(true));
    }

    @Override
    public long getAllActiveCustomersCount() {
        return repository.countByActiveTrue();
    }

    @Override
    public BigDecimal getAllActiveCustomersTotalCost(Long id) {
        Customer customer = getActiveCustomerById(id);
        if (customer == null || customer.getCart() == null) {
            return BigDecimal.ZERO;
        }

        return customer.getCart().getItems().stream()
                .map(position -> position.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(position.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getAllActiveCustomersAveragePrice(Long id) {
        Customer customer = getActiveCustomerById(id);
        if (customer == null || customer.getCart() == null || customer.getCart().getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = getAllActiveCustomersTotalCost(id);

        int totalQuantity = customer.getCart().getItems().stream()
                .mapToInt(Position::getQuantity)
                .sum();

        return totalCost.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
    }

    @Override
    public void addProductToBasketById(Long customerId, long productId, Integer quantity) {

    }

    @Override
    public void removeProductToBasketById(Long customerId, Long productId, Integer quantity) {

    }

    @Override
    @Transactional
    public void clearBasket(Long customerId) {
        Customer customer = getActiveCustomerById(customerId);
        if (customer != null && customer.getCart() != null) {
            customer.getCart().getItems().clear(); // Просто очищаємо список позицій
        }
    }
}

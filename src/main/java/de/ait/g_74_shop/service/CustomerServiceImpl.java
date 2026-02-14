package de.ait.g_74_shop.service;

import de.ait.g_74_shop.domain.Cart;
import de.ait.g_74_shop.domain.Customer;
import de.ait.g_74_shop.domain.Position;
import de.ait.g_74_shop.domain.Product;
import de.ait.g_74_shop.dto.customer.CustomerDto;
import de.ait.g_74_shop.dto.customer.CustomerSaveDto;
import de.ait.g_74_shop.dto.customer.CustomerUpdateDto;
import de.ait.g_74_shop.dto.mapping.CustomerMapper;
import de.ait.g_74_shop.dto.position.PositionUpdateDto;
import de.ait.g_74_shop.repository.CustomerRepository;
import de.ait.g_74_shop.service.interfaces.CustomerService;
import de.ait.g_74_shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository repository;
    private final ProductService productService;
    private final CustomerMapper mapper;

    public CustomerServiceImpl(CustomerRepository repository, ProductService productService, CustomerMapper mapper) {
        this.repository = repository;
        this.productService = productService;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CustomerDto save(CustomerSaveDto saveDto) {
        Customer entity = mapper.mapDtoToEntity(saveDto);

        Cart cart = new Cart();
        cart.setCustomer(entity);
        entity.setCart(cart);

        entity.setActive(true);
        repository.save(entity);

        // прописуємо подію logger вручну
        logger.info("Customer saved to the database: {}", entity);

        return mapper.mapEntityToDto(entity);
    }

    @Override
    public List<CustomerDto> getAllActiveCustomers() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .toList();
    }

    private Customer getActiveEntityById(Long id) {
        return repository.findByIdAndActiveTrue(id).orElse(null);
    }

    @Override
    public CustomerDto getActiveCustomerById(Long id) {
        Customer customer = getActiveEntityById(id);
        return customer == null ? null : mapper.mapEntityToDto(customer);
    }

    @Override
    @Transactional
    public void update(Long id, CustomerUpdateDto updateDto) {
        repository.findById(id)
                .ifPresent(x -> x.setName(updateDto.getNewName()));

        // прописуємо подію logger вручну
        logger.info("Customer id {} updated, new name: {}", id, updateDto.getNewName());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Customer customer = getActiveEntityById(id);
        if (customer == null) {
            return;
        }
        customer.setActive(false);

        // прописуємо подію logger вручну
        logger.info("Customer id {} marked as inactive", id);
    }

    @Override
    @Transactional
    public void restoreById(Long id) {
        repository.findById(id)
                .ifPresent(x -> x.setActive(true));

        // прописуємо подію logger вручну
        logger.info("Customer id {} marked as active", id);
    }

    @Override
    public long getAllActiveCustomersCount() {
        return repository.countByActiveTrue();
    }

    private List<Position> getCustomerActivePositions(Customer customer) {
        return customer.getCart()
                .getPositions()
                .stream()
                .filter(x -> x.getProduct().isActive())
                .toList();
    }

    private BigDecimal getPositionsTotalCost(List<Position> positions) {
        return positions.stream()
                .map(x -> x.getProduct().getPrice().multiply(BigDecimal.valueOf(x.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private int getProductsCountInPositions(List<Position> positions) {
        return positions.stream()
                .map(Position::getQuantity)
                .reduce(Integer::sum)
                .orElse(0);
    }

    @Override
    public BigDecimal getCustomerCartTotalCost(Long id) {
        Customer customer = getActiveEntityById(id);

        if (customer == null) {
            return BigDecimal.ZERO;
        }

        return getPositionsTotalCost(getCustomerActivePositions(customer));
    }

    @Override
    public BigDecimal getCustomerProductsAveragePrice(Long id) {
        Customer customer = getActiveEntityById(id);

        if (customer == null) {
            return BigDecimal.ZERO;
        }

        List<Position> activePositions = getCustomerActivePositions(customer);
        int productsQuantity = getProductsCountInPositions(activePositions);

        return productsQuantity == 0
                ? BigDecimal.ZERO
                : getPositionsTotalCost(activePositions).divide(BigDecimal.valueOf(productsQuantity), 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public void addProductToCustomerCart(Long customerId, Long productId, PositionUpdateDto positionUpdateDto) {
        Customer customer = getActiveEntityById(customerId);
        Product product = productService.getActiveEntityById(productId);

        if (customer == null || product == null) {
            return;
        }

        for (Position position : customer.getCart().getPositions()) {
            if (position.getProduct().equals(product)) {
                position.setQuantity(position.getQuantity() + positionUpdateDto.getQuantity());
                return;
            }
        }

        Cart cart = customer.getCart();
        Position position = new Position(product, positionUpdateDto.getQuantity(), cart);
        cart.getPositions().add(position);

        // прописуємо подію logger вручну
        logger.info("Product ID {} added to Cart of Customer ID {}. Quantity: {}",
                productId, customerId, positionUpdateDto.getQuantity());
    }

    @Override
    @Transactional
    public void removeProductFromCustomerCart(Long customerId, Long productId, PositionUpdateDto positionUpdateDto) {
        Customer customer = getActiveEntityById(customerId);
        Product product = productService.getActiveEntityById(productId);

        if (customer == null || product == null) {
            return;
        }

        Iterator<Position> iterator = customer.getCart().getPositions().iterator();
        while (iterator.hasNext()) {
            Position position = iterator.next();
            if (position.getProduct().equals(product)) {
                if (position.getQuantity() > positionUpdateDto.getQuantity()) {
                    position.setQuantity(position.getQuantity() - positionUpdateDto.getQuantity());
                } else {
                    iterator.remove();

                    // прописуємо подію logger вручну
                    logger.info("Product ID {} removed/reduced in Cart of Customer ID {}. Reduction quantity: {}",
                            productId, customerId, positionUpdateDto.getQuantity());
                }
                break;
            }
        }


    }

    @Override
    @Transactional
    public void clearCustomerCart(Long id) {
        Customer customer = getActiveEntityById(id);

        if (customer == null) {
            return;
        }

        customer.getCart().getPositions().clear();

        // прописуємо подію logger вручну
        logger.info("Cart for Customer ID {} has been fully cleared.", id);
    }
}

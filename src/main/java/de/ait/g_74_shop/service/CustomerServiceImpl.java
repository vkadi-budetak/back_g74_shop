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
import de.ait.g_74_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_74_shop.exceptions.types.EntityUpdateException;
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
import java.util.Objects;

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
        if (saveDto == null) {
            throw new EntityUpdateException("Customer data cannot be null");
        }

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
//        Objects.requireNonNull(id, "Customer id cannot be null");
        if (id == null) {
            throw new EntityUpdateException("Customer data cannot be null");
        }

        return repository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException(Customer.class, id)
        );
    }

    @Override
    public CustomerDto getActiveCustomerById(Long id) {
        Customer customer = getActiveEntityById(id);
        return customer == null ? null : mapper.mapEntityToDto(customer);
    }

    @Override
    @Transactional
    public void update(Long id, CustomerUpdateDto updateDto) {
//        Objects.requireNonNull(id, "Customer id cannot be null");
//        Objects.requireNonNull(updateDto, "Customer CustomerUpdateDto cannot be null");
        if (id == null) {
            throw new EntityUpdateException("Customer ID cannot be null");
        }
        if (updateDto == null) {
            throw new EntityUpdateException("Update data cannot be null");
        }



        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Customer.class, id))
                .setName(updateDto.getNewName());

        // прописуємо подію logger вручну
        logger.info("Customer id {} updated, new name: {}", id, updateDto.getNewName());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        getActiveEntityById(id).setActive(false);

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
        if (positionUpdateDto == null) {
            throw new EntityUpdateException("Position data cannot be null");
        }

        // 2. Отримуємо сутності (якщо їх нема - вилетить EntityNotFoundException)
        Customer customer = getActiveEntityById(customerId);
        Product product = productService.getActiveEntityById(productId);

        // 3. Валідація бізнес-логіки
        if (positionUpdateDto.getQuantity() <= 0) {
            throw new EntityUpdateException("Quantity must be positive");
        }

        // 4. Шукаємо, чи є вже такий товар у кошику
        for (Position position : customer.getCart().getPositions()) {
            if (position.getProduct().equals(product)) {
                position.setQuantity(position.getQuantity() + positionUpdateDto.getQuantity());
                logger.info("Updated quantity for Product ID {} in Cart of Customer ID {}", productId, customerId);
                return;
            }
        }

        // 5. Якщо товару ще немає - створюємо нову позицію
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
        if (positionUpdateDto == null) {
            throw new EntityUpdateException("PositionUpdateDto cannot be null");
        }

        // 2. Отримуємо сутності. Якщо їх немає - коди "вибухне" EntityNotFoundException автоматично
        Customer customer = getActiveEntityById(customerId);
        Product product = productService.getActiveEntityById(productId);

        // 3. Шукаємо позицію в кошику
        Iterator<Position> iterator = customer.getCart().getPositions().iterator();
        boolean found = false;

        while (iterator.hasNext()) {
            Position position = iterator.next();
            if (position.getProduct().equals(product)) {
                found = true;

                int currentQuantity = position.getQuantity();
                int quantityToRemove = positionUpdateDto.getQuantity();

                // 4. ПЕРЕВІРКА: чи не намагається клієнт видалити більше, ніж має?
                if (quantityToRemove > currentQuantity) {
                    throw new EntityUpdateException(String.format(
                            "Cannot remove %d items. Only %d items of product %s found in cart",
                            quantityToRemove, currentQuantity, product.getTitle()));
                }

                // 5. Логіка видалення або зменшення
                if (currentQuantity > quantityToRemove) {
                    position.setQuantity(currentQuantity - quantityToRemove);
                    logger.info("Product ID {} quantity reduced by {} for Customer ID {}",
                            productId, quantityToRemove, customerId);
                } else {
                    iterator.remove();

                    // прописуємо подію logger вручну
                    logger.info("Product ID {} removed/reduced in Cart of Customer ID {}. Reduction quantity: {}",
                            productId, customerId, positionUpdateDto.getQuantity());
                }
                break;
            }
        }
        // 6. Якщо пройшли весь цикл і не знайшли товар у кошику
        if (!found) {
            throw new EntityUpdateException("Product with id " + productId + " is not in the customer's cart");
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

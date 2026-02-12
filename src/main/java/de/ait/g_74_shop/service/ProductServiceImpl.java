package de.ait.g_74_shop.service;

import de.ait.g_74_shop.domain.Product;
import de.ait.g_74_shop.repository.ProductRepository;
import de.ait.g_74_shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/*
Что происходит при старте приложения:
1. Спринг сканирует приложение и находит интерфейсы JPA репозиториев.
2. Data JPA генерирует класс нашего интерфейса репозитория с методами,
   в которых прописаны все нужные SQL-запросы в базу.
3. Спринг создаёт объект этого класса (репозитория) и помещает его в Спринг контекст.
4. Спринг сканирует приложение и видит класс ProductServiceImpl, помеченный
   аннотацией @Service.
5. Спринг создаёт объект этого класса, при этом используя конструктор
   public ProductServiceImpl(ProductRepository repository) (потому что другого нет)
6. Спринг видит, что у конструктора есть входящий параметр ProductRepository repository.
7. Спринг извлекает из Спринг контекста объект репозитория и передаёт его в конструктор.
8. Конструктор записывает объект репозитория в поле
   private final ProductRepository repository;
 */


// Створюємо анотація і кажимо що він сервіс (Ти кажеш Spring: "Дивись, цей клас — це сервіс. Створи його сам,
// керуй ним, і якщо якомусь контролеру знадобиться CustomerService, дай йому саме цей об'єкт".)
@Service
public class ProductServiceImpl implements ProductService {

    // ми пишемо поле репозиотрія, але самі не ств обєкт репозиторія. Він ств при запуску проекта самостійно!
    private final ProductRepository repository;

    // ств конструктор
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        // коли зберігаємо продукт в базу значить він спочатку активний
        product.setActive(true);
        return repository.save(product);
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return repository.findAllByActiveTrue();
        // прописуємо у репозиторій ProductRepository
        // інструкцію findAllByActiveTrue(), вона буде фільтрувати activ продуктів
    }

    @Override
    public Product getActiveProductById(Long id) {
        // в репозиторію ств findByIdAndActiveTrue
        return repository.findByIdAndActiveTrue(id).orElse(null);
        // orElse - повертає сам продукт якщо він знайшовся або null якщо він знайшовся але не автивний
    }

    @Override
    // навішуємо обовязково додаткову анатоцію - транзакції(тобто щоб відпрацювала функція вірно
    // і в базі і в обєкті потрібно прописати цю транзакцію)
    @Transactional
    public void update(Long id, Product product) {
        repository.findById(id)
                .ifPresent(x -> x.setPrice(product.getPrice()));
        // findById(id): Spring Data JPA йде в базу і шукає рядок з цим ID. Повертає Optional<Customer>
        // ifPresent - якщо є щось в коробці (Option), тобто продукт знайдений,
        // до нього відпрацьовує функція. Якщо нічого не знайшлося, то ifPresent не відпрацює.
    }

    @Override
    @Transactional // Transactional також використовуємо її щоб не закрилася транзакція після product
    // а і ще відпрацювала і далі і внесла зміни у базі
    public void deleteById(Long id) {
        // ми повинні переключити на fals , тобто продукт повинен бути неактивний
        Product product = getActiveProductById(id); // ми знаходимо продукт в базі
        if (product == null) {
            return;
        }
        product.setActive(false);

    }

    @Override
    public void restoreById(Long id) {
        repository.findById(id)
                .ifPresent(x -> x.setActive(true));

    }

    @Override
    public long getAllActiveProductsCount() {
        // getAllActiveProducts весь ліст продуктів і добавити .size() але це неефективно
//        return getAllActiveProducts().size();

        // в репозиторї прописали countByActiveTrue() і звертаємся
        return repository.countByActiveTrue();
    }

    @Override
    public BigDecimal getAllActiveProductsTotalCost() {
        return getAllActiveProducts()
                .stream()
                .map(Product::getPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getAllActiveProductsAveragePrice() {
        long productCount = getAllActiveProductsCount(); // запрос на к-сть всіх активних продукти

        // робимо перевірку якщо продуктів 0
        if (productCount == 0) {
            return BigDecimal.ZERO;
        }
        return getAllActiveProductsTotalCost().divide(
                BigDecimal.valueOf(productCount),
                2, RoundingMode.HALF_UP);

        // сума всых продуктів ділемо(divide) на їх к-сть округляємо до 2 знаків,
        // а сам метод округлення називається RoundingMode.HALF_UP)
    }
}

package de.ait.g_74_shop.service;

import de.ait.g_74_shop.domain.Product;
import de.ait.g_74_shop.dto.mapping.ProductMapper;
import de.ait.g_74_shop.dto.product.ProductDto;
import de.ait.g_74_shop.dto.product.ProductSaveDto;
import de.ait.g_74_shop.dto.product.ProductUpdateDto;
import de.ait.g_74_shop.exceptions.types.EntityNotFoundException;
import de.ait.g_74_shop.repository.ProductRepository;
import de.ait.g_74_shop.service.interfaces.FileService;
import de.ait.g_74_shop.service.interfaces.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

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

    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class); // в якому клысі відбуваться подія

    // ми пишемо поле репозиотрія, але самі не ств обєкт репозиторія. Він ств при запуску проекта самостійно!
    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final FileService fileService;

    // ств конструктор
    public ProductServiceImpl(ProductRepository repository, ProductMapper mapper, FileService fileService) {
        this.repository = repository;
        this.mapper = mapper;
        this.fileService = fileService;
    }

    @Override
    public ProductDto save(ProductSaveDto saveDto) {
        Objects.requireNonNull(saveDto, "ProductSaveDto cannot be null");

        // коли зберігаємо продукт в базу значить він спочатку активний
        Product entity = mapper.mapDtoToEntity(saveDto);
        entity.setActive(true);
        repository.save(entity);

        // Не всегда стоит логировать обьект целикомм так как он может быть
        // очень большим. Иногда стоит логировать только определенные
        // параметры обьекта или вообще только его идентификатор
        // прописуємо подію logger вручну
        logger.info("Product saved to the database: {}", entity);

        return mapper.mapEntityToDto(entity);
    }

    @Override
    public List<ProductDto> getAllActiveProducts() {
        // прописуємо у репозиторій ProductRepository
        // інструкцію findAllByActiveTrue(), вона буде фільтрувати activ продуктів
        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::mapEntityToDto)
                .toList();
    }

    public Product getActiveEntityById(Long id) {
        Objects.requireNonNull(id, "Product id cannot be null"); // перевіряємо вхідні параметри

        // в репозиторію ств findByIdAndActiveTrue
//        return repository.findByIdAndActiveTrue(id).orElse(null);
        // orElse - повертає сам продукт якщо він знайшовся або null якщо він знайшовся але не автивний
        return repository.findByIdAndActiveTrue(id).orElseThrow(
                () -> new EntityNotFoundException(Product.class, id)
        );
    }


    @Override
    public ProductDto getActiveProductById(Long id) {
//        Product entity = getActiveEntityById(id);
//        ProductDto dto = mapper.mapEntityToDto(entity);
//        return dto;

        // альтернативный варинт в одну строку
        return mapper.mapEntityToDto(getActiveEntityById(id));
    }

    @Override
    // навішуємо обовязково додаткову анатоцію - транзакції(тобто щоб відпрацювала функція вірно
    // і в базі і в обєкті потрібно прописати цю транзакцію)
    @Transactional
    public void update(Long id, ProductUpdateDto updateDto) {
        Objects.requireNonNull(id, "Product id cannot be null");
        Objects.requireNonNull(updateDto, "Product ProductUpdateDto cannot be null");

        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, id))
                .setPrice(updateDto.getNewPrice());

//                .ifPresent(x -> x.setPrice(updateDto.getNewPrice()));
        // findById(id): Spring Data JPA йде в базу і шукає рядок з цим ID. Повертає Optional<Customer>
        // ifPresent - якщо є щось в коробці (Option), тобто продукт знайдений,
        // до нього відпрацьовує функція. Якщо нічого не знайшлося, то ifPresent не відпрацює.


        // прописуємо подію logger вручну
        logger.info("Product id {} updated, new price: {}", id, updateDto.getNewPrice());
    }

    @Override
    @Transactional // Transactional також використовуємо її щоб не закрилася транзакція після product
    // а і ще відпрацювала і далі і внесла зміни у базі
    public void deleteById(Long id) {
        // ми повинні переключити на fals , тобто продукт повинен бути неактивний
        getActiveEntityById(id).setActive(false);
        ; // ми знаходимо продукт в базі

        // прописуємо подію logger вручну
        logger.info("Product id {} marked as inactive", id);

    }

    @Override
    public void restoreById(Long id) {
        Objects.requireNonNull(id, "Product id cannot be null");

        repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Product.class, id))
                .setActive(true);

        // прописуємо подію logger вручну
        logger.info("Product id {} marked as active", id);
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
        return repository.findAllByActiveTrue()
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

        // сума всіх продуктів ділимо(divide) на їх к-сть округляємо до 2 знаків,
        // а сам метод заокркглення називається RoundingMode.HALF_UP)
    }

    @Override
    @Transactional
    public void addImage(long id, MultipartFile image) throws IOException {
        Objects.requireNonNull(id, "Product id cannot be null");

        Product product = getActiveEntityById(id);
        String imageUrl = fileService.uploadAndGetUrl(image);
        // Здесь будет обращение к сервису и получения ссылке на файл
        // Здесь будет присвоение этой ссылке нашему продукту
        product.setImageUrl(imageUrl);
    }
}

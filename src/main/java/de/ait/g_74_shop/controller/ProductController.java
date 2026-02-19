package de.ait.g_74_shop.controller;

import de.ait.g_74_shop.dto.product.ProductDto;
import de.ait.g_74_shop.dto.product.ProductSaveDto;
import de.ait.g_74_shop.dto.product.ProductUpdateDto;
import de.ait.g_74_shop.service.interfaces.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

// @RestController - під капотом відпрацьовують методи при запросах
// @RequestMapping("/products") - ця анотація говорить спрінгу про те що якщо прийшов запрос на http://10.20.30.40:8080/products...
// то цей запрос потрібно адресувати саме на ProductController
@RestController
@RequestMapping("/products")
public class ProductController {

    // прописуємо поле із сервісом, і не створюємо обєкт як і у ProductServiceImpl
    private final ProductService service;

    // ств під ProductService конструктор
    public ProductController(ProductService service) {
        this.service = service;
    }

    /// Сохранить продукт в базе данных (при сохранении продукт автоматически считается активным).
    /// POST -> http://10.20.30.40:8080/products ожидаем тело запроса
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // щоб приходив статус 201 Created, а не 200
    // прописуємо анотацію RequestBody - спринг розуміє що коли він отримає запрос(тіло обєкта) і передає джексону....
    public ProductDto save(@RequestBody ProductSaveDto saveDto) {
//        System.out.println("На вход пришел продукт:" + product);
//        return null;
        return service.save(saveDto);
    }

    /// Вернуть все продукты из базы данных (активные).
    /// GET -> http://10.20.30.40:8080/products
    @GetMapping
    public List<ProductDto> getAll() {
//        System.out.println("Запрошены все продукты");
//        return null;
        return service.getAllActiveProducts();
    }

    /// Вернуть один продукт из базы данных по его идентификатору (если он активен).
    /// GET -> http://10.20.30.40:8080/products/5
    // @GetMapping("/{id}") - інструкція - при виклику id взяти передану цифру і передати в параметр
    @GetMapping("/{id}")
    // Використаємо анатоцію @PathVariable - відповідає за змінну шляху
    public ProductDto getById(@PathVariable Long id) {
//        System.out.println("Запрошен продукт с ид " + id);
//        return null;
        return service.getActiveProductById(id);
    }

    /// Изменить один продукт в базе данных по его идентификатору.
    /// PUT -> http://10.20.30.40:8080/products/5 -> ожидаем тело с новыми значениями для абдейта
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ProductUpdateDto updateDto) {
//        System.out.println("Пришел запрос на изменения продукта с ид " + id);
//        System.out.println("Новая цена продукта - " + product.getPrice());
        service.update(id, updateDto);
    }

    /// Удалить продукт из базы данных по его идентификатору.
    // ОБОВЯЗКОВО при видаленні прописуємо анотацію на 204 статус - @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    /// Восстановить удалённый продукт в базе данных по его идентификатору.
    /// PUT -> http://10.20.30.40:8080/products/5/restore
    @PutMapping("/{id}/restore")
    public void restoreById(@PathVariable Long id) {
        service.restoreById(id);
    }

    /// Вернуть общее количество продуктов в базе данных (активных).
    /// GET -> http://10.20.30.40:8080/products/count
    @GetMapping("/count")
    public long getProductQuantity() {
//        return 0;
        return service.getAllActiveProductsCount();
    }

    /// Вернуть суммарную стоимость всех продуктов в базе данных (активных).
    /// GET -> http://10.20.30.40:8080/products/total-cost
    @GetMapping("/total-cost")
    public BigDecimal getProductsTotalCost() {
        return service.getAllActiveProductsTotalCost();
    }

    /// Вернуть среднюю стоимость продукта в базе данных (из активных).
    /// GET -> http://10.20.30.40:8080/products/avg
    @GetMapping("/avg")
    public BigDecimal getProductAveragePrice() {
        return service.getAllActiveProductsAveragePrice();
    }


    // Добавить картинку к конкретному продукту по идентификатору
    // POST -> http://10.20.30.40:8080/products/5/image
    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public void addImage(@PathVariable Long id, @RequestParam MultipartFile image) {
        service.addImage(id, image);
    }
}

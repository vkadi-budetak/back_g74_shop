package de.ait.g_74_shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


import java.math.BigDecimal;

/// Клас Продукт

@Entity // Entity - анатоції - означає що даний продукт його обєкти зберігається в базі даних
@Table(name = "product") // в якій саме таблиці лежать

public class Product {

    @Id // jakarta.persistence
    @GeneratedValue(strategy = GenerationType.IDENTITY) // база даних сама генерує id
    @Column(name = "id") // де саме лежить id в якій колонці
    private Long id;

    /* Використувуємо Pattern
    Допустим мы хотим чтобы названия продуктов отвечаол след требованием:
    1  Первая буква быть в верхнем регистре
    2  Остальные буквы должны быть в нижнем регистре
    3  Название продуктов должно быть не короче трех букв
    4  Должно быть нельзя испольщовать спец символы и цифри. (Пробел можно)
     */
    @NotNull(message = "Product title cannot be null")
    @NotBlank(message = "Product title cannot be empty")
//    @Length(min = 3, max = 50)
    @Pattern(
            regexp = "[A-Z][a-z ]{2,}",
            message = "Product title should be at least three characters length and start with capital letter"
    )
    @Column(name = "title")
    private String title;


    @NotNull(message = "Product price cannot be null")
    @DecimalMin(
            value = "0.0",
            message = "Product price should be greater or equal than 0")
    @DecimalMax(
            value = "1000.00",
            inclusive = false, // означає що 1000 не включена
            message = "Product price should be lesser than 1000"
    )
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "active")
    private boolean active; // прописуємо чи в продажі товар чи ні

    //    генеруємо пустий конструктор для jekson
    public Product() {
    }

    // Генеруємо гетери і сетери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    // Коли генеруємо equals і hashCode потрібно вибрати:
    // Вибрати instance expression -> вибираємо тільки id: Long
    // і прописуємо  if (this == o) { return true;}
    // і в пишу в return - id !=null ....
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        // тут порінвюємо продукт із другим класом
        if (!(o instanceof Product product)) {
            return false;
        }

        return id != null && id.equals(product.id);
    }

    // Змінюю return getClass().hashCode() вписуємо getClass
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // toString пишемо руками щоб не було циклічності
    // %d - ціле число
    // %s - рядкову змінну
    // %.2f - не ціле число 2 знаки після коми
    @Override
    public String toString() {
        // Product: id - 5, title - Banana, price - 100.00, active - yes
        return String.format("Product: id - %d, title - %s, price - %.2f, active - %s",
                id, title, price, active ? "yes" : "no");
    }
}


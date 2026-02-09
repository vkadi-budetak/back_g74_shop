package de.ait.g_74_shop.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

/// Клас Продукт

@Entity // Entity - анатоції - означає що даний продукт його обєкти зберігається в базі даних
@Table(name = "product") // в якій саме таблиці лежать

public class Product {

    @Id // jakarta.persistence
    @GeneratedValue(strategy = GenerationType.IDENTITY) // база даних сама генерує id
    @Column(name = "id") // де саме лежить id в якій колонці
    private Long id;

    @Column(name = "title")
    private String title;

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


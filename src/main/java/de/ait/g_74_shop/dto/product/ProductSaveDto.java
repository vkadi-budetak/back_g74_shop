package de.ait.g_74_shop.dto.product;

import java.math.BigDecimal;

// ми створюємо клас для зберіння нового продукта в базу
public class ProductSaveDto {

    // прописуємо поля які хочемо отримувати від користувача!
    private String title;
    private BigDecimal price;

    public ProductSaveDto() {
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

    @Override
    public String toString() {
        // Product: id - 5, title - Banana, price - 100.00, active - yes
        return String.format("Product Save DTO: title - %s, price - %.2f, ",
                title, price);
    }
}

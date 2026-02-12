package de.ait.g_74_shop.dto.product;

import java.math.BigDecimal;

// служить для передачі даних клієнту
public class ProductDto {

    // прописуємо які поля ми будемо віддавати клієнту
    private Long id;
    private String title;
    private BigDecimal price;

    // конструктор
    public ProductDto() {
    }

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

    @Override
    public String toString() {
        return String.format("Product DTO: id - %d, title - %s, price - %.2f",
                id, title, price);
    }
}

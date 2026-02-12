package de.ait.g_74_shop.dto.product;

import java.math.BigDecimal;

// ми створюємо клас для обновлення інфи в базі
public class ProductUpdateDto {

    // від клієнта ми оцікуємо тільки ціну
    private BigDecimal newPrice;

    public ProductUpdateDto() {
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return String.format("Product Update DTO: new price - %.2f", newPrice);
    }
}


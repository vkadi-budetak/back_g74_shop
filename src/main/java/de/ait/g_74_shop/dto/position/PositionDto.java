package de.ait.g_74_shop.dto.position;

import de.ait.g_74_shop.dto.product.ProductDto;

public class PositionDto {

    private ProductDto product;
    private int quantity;

    public PositionDto() {
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("Position DTO: product - %s, quantity - %d", product, quantity);
    }
}

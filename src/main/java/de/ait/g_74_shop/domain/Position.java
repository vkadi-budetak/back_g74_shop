package de.ait.g_74_shop.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity // Entity - анатоції - означає що даний продукт його обєкти зберігається в базі даних
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // прописується позиція - m:1 (EAGER або LAZY - жадна або лінива загрузка)
    @JoinColumn(name = "product_id", nullable = false)
    // колонка називаєтсья - "product_id". анотація JoinColumn - яке саме поле буде посиланням на продукт. nullable = false - заборонені пусті значення
    private Product product;

    @Column(name = "quantity")
    private int quantity; // к-сть продуктів

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false) // колонка називаєтсья - cart_id
    private Cart cart; // в якій корзині продукт лежить

    // конструктор для jekson
    public Position() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Position position)) {
            return false;
        }

        return id != null && Objects.equals(id, position.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Position: id - %d, product - %s, quantity - %d", id, product, quantity);
    }
}

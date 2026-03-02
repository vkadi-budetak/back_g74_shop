package de.ait.g_74_shop.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirmation_code")
public class ConfirmationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value")
    private String value;

    @Column(name = "expiration")
    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private User user;

    public ConfirmationCode() {
    }

    public ConfirmationCode(String value, LocalDateTime expiration, User user) {
        this.value = value;
        this.expiration = expiration;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ConfirmationCode confirmationCode)) {
            return false;
        }

        return id != null && id.equals(confirmationCode.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        // Product: id - 5, title - Banana, price - 100.00, active - yes
        return String.format("ConfirmationCode: id - %d, value - %s, expiration - %s, user email - %s"
                , id,
                value,
                expiration == null ? "unknown" : expiration,
                user == null ? "unknown" : user.getEmail());
    }
}

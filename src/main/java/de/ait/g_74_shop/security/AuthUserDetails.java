package de.ait.g_74_shop.security;

import de.ait.g_74_shop.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AuthUserDetails implements UserDetails {

    private User user; // ств клас адаптер

    public AuthUserDetails(User user) {
        this.user = user;
    }

    // метод викликає колекцію ролей
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_ADMIN -> SimpleGrantedAuthority
        // user.getRole().name() -> обьект класса Role -> "ROLE_ADMIN"
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // метод чи підтвердив реєстрацію чи ні
    @Override
    public  boolean isEnabled(){
        return user.isConfirmed();
    }
}

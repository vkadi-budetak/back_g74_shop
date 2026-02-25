package de.ait.g_74_shop.service;

import de.ait.g_74_shop.domain.User;
import de.ait.g_74_shop.repository.UserRepository;
import de.ait.g_74_shop.security.AuthUserDetails;
import de.ait.g_74_shop.service.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    // буде при виклику цього методу діставати юзера із бази і пароль
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", email)));

        return new AuthUserDetails(user);
    }

}

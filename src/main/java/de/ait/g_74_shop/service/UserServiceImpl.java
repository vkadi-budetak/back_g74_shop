package de.ait.g_74_shop.service;

import de.ait.g_74_shop.domain.User;
import de.ait.g_74_shop.domain.enums.Role;
import de.ait.g_74_shop.dto.user.UserRegistrationDto;
import de.ait.g_74_shop.exceptions.types.RegistrationException;
import de.ait.g_74_shop.repository.UserRepository;
import de.ait.g_74_shop.security.AuthUserDetails;
import de.ait.g_74_shop.service.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // буде при виклику цього методу діставати юзера із бази і пароль
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", email)));

        return new AuthUserDetails(user);
    }

    @Override
    public void register(UserRegistrationDto registrationDto) {
        /*
        Возможные сценарии регистрации пользователя
        1. Пользователя еще нет в БД (он пришел к нам первый раз)
        2. Не первая попытка регистрации (email в БД есть, confirmed - false)
        3. Попытка регистрации на email который уже подтвержден (email в БД есть, confirmed - true)
         */

        String email = registrationDto.getEmail();
        User user = repository.findByEmail(email).orElse(null);

        if (user == null) {
            // 1 сценарий

            user = new User();
            user.setEmail(email);
            user.setRole(Role.ROLE_USER);
            user.setConfirmed(false);

        } else if (user.isConfirmed()) {
            // 3 сценарий
            throw new RegistrationException(String.format("Email %s already is use", email));
        }

        // Общие действия для сценариев 1 и 2
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());

        repository.save(user);
        // отправляем пользователю письмо о том что он должен подтвердить регистрацию
    }
}

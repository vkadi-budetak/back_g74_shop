package de.ait.g_74_shop.service.interfaces;

import de.ait.g_74_shop.dto.user.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

// ціль інтерфейса отримати дані і проконтролювати ці дані(імя, пароль....)
public interface UserService extends UserDetailsService {

    void register(UserRegistrationDto registrationDto);

}

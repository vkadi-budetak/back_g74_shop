package de.ait.g_74_shop.service.interfaces;

import de.ait.g_74_shop.domain.User;

public interface EmailService {

    void sendConfirmationEmail(User user);
}

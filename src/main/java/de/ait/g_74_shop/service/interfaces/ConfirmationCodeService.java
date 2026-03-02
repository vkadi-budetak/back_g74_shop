package de.ait.g_74_shop.service.interfaces;

import de.ait.g_74_shop.domain.User;

public interface ConfirmationCodeService {

    String generateConfirmationCode(User user);
}

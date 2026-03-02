package de.ait.g_74_shop.service;

import de.ait.g_74_shop.domain.ConfirmationCode;
import de.ait.g_74_shop.domain.User;
import de.ait.g_74_shop.repository.ConfirmationCodeRepository;
import de.ait.g_74_shop.service.interfaces.ConfirmationCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationCodeServiceImpl implements ConfirmationCodeService {

    private final ConfirmationCodeRepository repository;

    public ConfirmationCodeServiceImpl(ConfirmationCodeRepository repository) {
        this.repository = repository;
    }

    @Override
    public String generateConfirmationCode(User user) {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(24);
        String value = UUID.randomUUID().toString();
        ConfirmationCode entity = new ConfirmationCode(value, expiration, user);
        repository.save(entity);
        return value;
    }
}

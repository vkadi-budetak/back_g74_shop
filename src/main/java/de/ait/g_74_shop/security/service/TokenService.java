package de.ait.g_74_shop.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    // шифровка base64 - генеруємо фразу для шифровання(знаходимо шифровальщик в google)

    // ств конструктор
    public TokenService(
            @Value("${KEY_PHRASE_ACCESS}") String accessPhrase,
            @Value("${KEY_PHRASE_REFRESH}") String refreshPhrase
    ) {
        accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessPhrase));
        refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshPhrase));
    }

    public String generateAccessToken(String email) {
        return generateToken(email, accessKey, 15 * 60 * 1000);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, refreshKey, 24 * 60 * 60 * 1000);
    }


    // генеруємо токен - універсальний, який вміє генерувати 2 токена
    private String generateToken(String email, SecretKey key, int expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(email)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    private boolean validateToken(String token, SecretKey key) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getAccessClaims(String accessToken) {
        return getClaims(accessToken, accessKey);
    }

    public Claims getRefreshClaims(String refreshToken) {
        return getClaims(refreshToken, refreshKey);
    }

    private Claims getClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

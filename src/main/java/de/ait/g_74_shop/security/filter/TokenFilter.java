package de.ait.g_74_shop.security.filter;

import de.ait.g_74_shop.security.service.TokenService;
import de.ait.g_74_shop.service.interfaces.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static de.ait.g_74_shop.constants.Constants.*;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    public TokenFilter(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenService.getTokenFromRequest(request, ACCESS_TOKEN_COOKIE_NAME);

        if (token != null && tokenService.validateAccessToken(token)) {
            Claims claims = tokenService.getAccessClaims(token);
            String email = claims.getSubject();
            UserDetails userDetails = userService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}

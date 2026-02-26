package de.ait.g_74_shop.security.controller;

import de.ait.g_74_shop.security.dto.LoginRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public void login(@RequestBody LoginRequestDto loginRequestDto) {
        System.out.println();
    }
}

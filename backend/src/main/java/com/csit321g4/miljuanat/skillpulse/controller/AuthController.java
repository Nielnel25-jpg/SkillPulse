package com.csit321g4.miljuanat.skillpulse.controller;

import com.csit321g4.miljuanat.skillpulse.dto.AuthDtos.*;
import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.UserResponse;
import com.csit321g4.miljuanat.skillpulse.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}

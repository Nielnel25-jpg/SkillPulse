package com.csit321g4.miljuanat.skillpulse.service;

import com.csit321g4.miljuanat.skillpulse.dto.AuthDtos.*;
import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.CreateUserRequest;
import com.csit321g4.miljuanat.skillpulse.dto.UserDtos.UserResponse;
import com.csit321g4.miljuanat.skillpulse.entity.*;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.exception.ApiException;
import com.csit321g4.miljuanat.skillpulse.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@Transactional
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final UserService userService;

    public AuthService(UserRepository users, PasswordEncoder encoder, UserService userService) {
        this.users = users;
        this.encoder = encoder;
        this.userService = userService;
    }

    /** Public self-registration for students and instructors. Admins cannot self-register. */
    public UserResponse register(RegisterRequest req) {
        if (req.role() == Role.ADMIN) {
            throw ApiException.badRequest("Self-registration as ADMIN is not allowed");
        }
        // Reuse existing create logic
        return userService.create(new CreateUserRequest(
                req.fullName() != null ? req.fullName().trim() : "",
                req.email() != null ? req.email().trim().toLowerCase() : "",
                req.password(),
                req.role(),
                req.studentNumber() != null ? req.studentNumber().trim() : null,
                req.department() != null ? req.department().trim() : null));
    }

    /**
     * Mock login: verifies email+password, returns a fake base64 token.
     * Replace with real JWT when security is added.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        String cleanEmail = req.email() == null ? "" : req.email().trim();
        User user = users.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (!user.isActive()) throw ApiException.unauthorized("Account is deactivated");
        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("Invalid email or password");
        }
        // Mock token: base64(id:email:role) – NOT secure, replace with JWT later
        String raw = user.getId() + ":" + user.getEmail() + ":" + user.getRole();
        String token = Base64.getEncoder().encodeToString(raw.getBytes());
        return new LoginResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}

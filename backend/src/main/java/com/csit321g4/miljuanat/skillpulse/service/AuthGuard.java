package com.csit321g4.miljuanat.skillpulse.service;

import com.csit321g4.miljuanat.skillpulse.entity.User;
import com.csit321g4.miljuanat.skillpulse.enums.Role;
import com.csit321g4.miljuanat.skillpulse.exception.ApiException;
import com.csit321g4.miljuanat.skillpulse.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * Resolves the caller from the Authorization header and enforces roles.
 * Works with the current mock token (base64 id:email:role). The user and role are always
 * re-checked against the database. Swap the token parsing for JWT validation later.
 */
@Component
public class AuthGuard {

    private final UserRepository users;

    public AuthGuard(UserRepository users) { this.users = users; }

    public User require(String authHeader, Role... allowed) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw ApiException.unauthorized("Missing authorization token");
        }
        long id;
        try {
            String raw = new String(Base64.getDecoder().decode(authHeader.substring(7).trim()), StandardCharsets.UTF_8);
            id = Long.parseLong(raw.split(":", 2)[0]);
        } catch (IllegalArgumentException e) {
            throw ApiException.unauthorized("Invalid token");
        }
        User user = users.findById(id).orElseThrow(() -> ApiException.unauthorized("Invalid token"));
        if (!user.isActive()) throw ApiException.unauthorized("Account is deactivated");
        if (allowed.length > 0 && Arrays.stream(allowed).noneMatch(r -> r == user.getRole())) {
            throw new ApiException(org.springframework.http.HttpStatus.FORBIDDEN, "You do not have access to this resource");
        }
        return user;
    }
}

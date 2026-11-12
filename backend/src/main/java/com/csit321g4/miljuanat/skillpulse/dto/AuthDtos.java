package com.csit321g4.miljuanat.skillpulse.dto;

import com.csit321g4.miljuanat.skillpulse.enums.Role;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank String fullName,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, message = "must be at least 8 characters") String password,
            @NotNull Role role,
            String studentNumber,
            String department) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

    public record LoginResponse(
            String token,
            Long id,
            String fullName,
            String email,
            Role role) {}
}

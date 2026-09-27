package org.example.identityservice.dto;


import org.example.identityservice.model.Role;

public record AuthResponse(

        String token,

        Long userId,

        String name,

        String email,

        Role role
) {
}


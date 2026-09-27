package org.example.identityservice.dto;



import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;

import java.time.LocalDateTime;

public record UserResponse(

        Long id,

        String name,

        String email,

        Role role,

        Boolean active,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {

    public static UserResponse from(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}


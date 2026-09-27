package org.example.identityservice.loader;


import org.example.identityservice.config.SeedUserProperties;
import org.example.identityservice.model.Role;

public record PreloadedUserData(

        String name,

        String email,

        String password,

        Role role
) {

    public static PreloadedUserData from(
            SeedUserProperties.SeedUser user
    ) {

        return new PreloadedUserData(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}

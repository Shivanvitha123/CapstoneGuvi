package org.example.identityservice.config;

import lombok.Data;
import org.example.identityservice.model.Role;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "identity")
public class SeedUserProperties {

    private List<SeedUser> preloadedUsers = new ArrayList<>();

    @Data
    public static class SeedUser {

        private String name;

        private String email;

        private String password;

        private Role role;
    }
}

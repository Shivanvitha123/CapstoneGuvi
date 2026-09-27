package org.example.identityservice.loader;
import org.example.identityservice.config.SeedUserProperties;
import org.example.identityservice.entity.User;
import org.example.identityservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Locale;

@Component
public class PreloadedUserLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedUserProperties seedUserProperties;

    public PreloadedUserLoader(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            SeedUserProperties seedUserProperties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedUserProperties = seedUserProperties;
    }

    @Override
    public void run(String... args) {

        Flux.fromIterable(seedUserProperties.getPreloadedUsers())
                .flatMap(this::createIfMissing)
                .collectList()
                .block();
    }

    private Mono<User> createIfMissing(
            SeedUserProperties.SeedUser seedUser
    ) {

        String email = seedUser.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        return userRepository.findByEmail(email)
                .hasElement()
                .flatMap(exists -> {

                    if (exists) {
                        return Mono.empty();
                    }

                    LocalDateTime now = LocalDateTime.now();

                    User user = User.builder()
                            .name(seedUser.getName())
                            .email(email)
                            .password(
                                    passwordEncoder.encode(
                                            seedUser.getPassword()
                                    )
                            )
                            .role(seedUser.getRole())
                            .active(true)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();

                    return userRepository.save(user);
                });
    }
}
package org.example.identityservice.repository;


import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@DataR2dbcTest
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.r2dbc.username=sa",
        "spring.r2dbc.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema.sql"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {

        User user = User.builder()
                .name("Test User")
                .email("test@risktwin.com")
                .password("hashed-password")
                .role(Role.BUSINESS_OWNER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        StepVerifier.create(
                        userRepository.save(user)
                )
                .assertNext(savedUser -> {

                    assert savedUser.getId() != null;

                    assert savedUser
                            .getEmail()
                            .equals("test@risktwin.com");
                })
                .verifyComplete();

        StepVerifier.create(
                        userRepository.findByEmail(
                                "test@risktwin.com"
                        )
                )
                .assertNext(foundUser -> {

                    assert foundUser.getRole()
                            == Role.BUSINESS_OWNER;
                })
                .verifyComplete();
    }
}

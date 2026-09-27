package org.example.identityservice.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    public SecurityConfig(
            JwtAuthenticationWebFilter jwtAuthenticationWebFilter
    ) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .httpBasic(
                        ServerHttpSecurity.HttpBasicSpec::disable
                )

                .formLogin(
                        ServerHttpSecurity.FormLoginSpec::disable
                )

                .logout(
                        ServerHttpSecurity.LogoutSpec::disable
                )

                .authorizeExchange(exchange -> exchange

                        .pathMatchers(
                                "/api/auth/login",
                                "/api/auth/register"
                        )
                        .permitAll()

                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        )
                        .permitAll()

                        .pathMatchers("/api/auth/me")
                        .authenticated()

                        .pathMatchers("/api/auth/users/**")
                        .hasRole("ADMIN")

                        .anyExchange()
                        .authenticated()
                )

                .addFilterAt(
                        jwtAuthenticationWebFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION
                )

                .build();
    }
}




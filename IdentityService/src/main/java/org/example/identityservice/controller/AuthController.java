package org.example.identityservice.controller;


import jakarta.validation.Valid;
import org.example.identityservice.dto.AuthResponse;
import org.example.identityservice.dto.LoginRequest;
import org.example.identityservice.dto.RegisterRequest;
import org.example.identityservice.dto.UserResponse;
import org.example.identityservice.service.AuthService;
import org.example.identityservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(
            AuthService authService,
            UserService userService
    ) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public Mono<UserResponse> getCurrentUser(
            Authentication authentication
    ) {

        Long userId = Long.valueOf(
                authentication.getName()
        );

        return userService.getUserById(userId);
    }

    @GetMapping("/users")
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public Mono<UserResponse> getUserById(
            @PathVariable Long id
    ) {
        return userService.getUserById(id);
    }
}

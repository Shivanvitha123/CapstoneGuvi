package org.example.identityservice.service;


import org.example.identityservice.dto.UserResponse;
import org.example.identityservice.exception.UserNotFoundException;
import org.example.identityservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserResponse> getUserById(Long id) {

        return userRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new UserNotFoundException(
                                        "User not found with id: " + id
                                )
                        )
                )
                .map(UserResponse::from);
    }

    public Mono<UserResponse> getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .switchIfEmpty(
                        Mono.error(
                                new UserNotFoundException(
                                        "User not found with email: " + email
                                )
                        )
                )
                .map(UserResponse::from);
    }

    public Flux<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .map(UserResponse::from);
    }
}


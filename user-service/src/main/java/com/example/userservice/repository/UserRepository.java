package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByEmail(String email);
    Mono<User> findByResetToken(String resetToken);
    Flux<User> findByActifTrue();
}

package com.example.userservice.service;

import com.example.userservice.dto.*;
import com.example.userservice.model.*;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll().map(this::toResponse);
    }

    public Mono<UserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(this::toResponse);
    }

    public Mono<UserResponse> createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        if (user.getRole() == null) user.setRole(Role.USER);
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(user)
                .doOnSuccess(u -> sendEvent(u, "USER_CREATED"))
                .map(this::toResponse);
    }

    public Mono<Void> forgotPassword(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    String token = UUID.randomUUID().toString();
                    user.setResetToken(token);
                    user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));
                    return userRepository.save(user)
                            .doOnSuccess(u -> sendPasswordResetEmail(u.getEmail(), token))
                            .then();
                });
    }

    public Mono<Void> resetPassword(String token, String newPassword) {
        return userRepository.findByResetToken(token)
                .filter(user -> user.getResetTokenExpiration().isAfter(LocalDateTime.now()))
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetToken(null);
                    user.setResetTokenExpiration(null);
                    return userRepository.save(user).then();
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Token invalide ou expiré")));
    }

    private void sendPasswordResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Pour réinitialiser votre mot de passe, utilisez ce jeton : " + token + 
                        "\nCe jeton expirera dans une heure.");
        mailSender.send(message);
        log.info("[MAIL] Email de réinitialisation envoyé à {}", email);
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.findById(id)
                .flatMap(u -> userRepository.deleteById(id).then(Mono.just(u)))
                .doOnSuccess(u -> sendEvent(u, "USER_DELETED"))
                .then();
    }

    private void sendEvent(User user, String type) {
        UserEvent event = UserEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .eventType(type)
                .timestamp(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend("user.exchange", "user.routingKey", event);
        log.info("[RABBITMQ] Event {} envoyé pour {}", type, user.getEmail());
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .actif(user.isActif())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

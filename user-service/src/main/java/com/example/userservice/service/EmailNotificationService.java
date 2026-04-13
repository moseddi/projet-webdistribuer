package com.example.userservice.service;

import com.example.userservice.config.RabbitMQConfig;
import com.example.userservice.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    // SCÉNARIO ASYNCHRONE 2 : Consommation des événements via RabbitMQ
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleUserEvent(UserEvent event) {
        log.info("[RABBITMQ CONSUMER] Événement reçu : {} pour {}", event.getEventType(), event.getEmail());

        switch (event.getEventType()) {
            case "USER_CREATED":
                sendWelcomeEmail(event.getEmail());
                break;
            case "USER_DELETED":
                sendGoodbyeEmail(event.getEmail());
                break;
            case "PASSWORD_RESET":
                sendPasswordResetEmail(event.getEmail());
                break;
            default:
                log.warn("Type d'événement inconnu : {}", event.getEventType());
        }
    }

    private void sendWelcomeEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("contact@trendly.com");
            message.setTo(toEmail);
            message.setSubject("Bienvenue chez Trendly Shop !");
            message.setText("Bonjour,\n\nMerci de vous être inscrit sur Trendly Shop. Découvrez dès maintenant nos dernières collections Luxe.\n\nL'équipe Trendly.");
            
            mailSender.send(message);
            log.info("Email de bienvenue envoyé à {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de bienvenue à {} : {}", toEmail, e.getMessage());
        }
    }

    private void sendGoodbyeEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("contact@trendly.com");
            message.setTo(toEmail);
            message.setSubject("Désinscription Trendly Shop");
            message.setText("Bonjour,\n\nVotre compte a bien été supprimé. Nous espérons vous revoir bientôt.\n\nL'équipe Trendly.");
            
            mailSender.send(message);
            log.info("Email de départ envoyé à {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de départ à {} : {}", toEmail, e.getMessage());
        }
    }

    private void sendPasswordResetEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("contact@trendly.com");
            message.setTo(toEmail);
            message.setSubject("Réinitialisation de votre mot de passe");
            message.setText("Bonjour,\n\nVous avez demandé à réinitialiser votre mot de passe. Veuillez cliquer sur le lien suivant (fictif) pour le réinitialiser.\n\nL'équipe Trendly.");
            
            mailSender.send(message);
            log.info("Email de réinitialisation envoyé à {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de reset à {}", toEmail);
        }
    }
}

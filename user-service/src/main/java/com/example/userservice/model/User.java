package com.example.userservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String ville;
    private String pays;
    private String password;
    private Role role;
    private boolean actif = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String resetToken;
    private LocalDateTime resetTokenExpiration;
}

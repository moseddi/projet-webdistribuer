package com.example.commandeservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Désactivation de CSRF
                .authorizeHttpRequests(authorize -> authorize
                        // Le dashboard stats est temporairement accessible à tout utilisateur connecté
                        .requestMatchers(HttpMethod.GET, "/api/commandes/stats").authenticated()
                        // Les users peuvent créer des commandes
                        .requestMatchers(HttpMethod.POST, "/api/commandes/**").hasAuthority("user")
                        // Les admins peuvent supprimer et modifier les statuts des commandes
                        .requestMatchers(HttpMethod.DELETE, "/api/commandes/**").hasAuthority("admin")
                        .requestMatchers(HttpMethod.PUT, "/api/commandes/**").hasAuthority("admin")
                        // Toute autre requête nécessite d'être authentifié (ex: GET, etc.)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));
        return http.build();
    }
}

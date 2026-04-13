package com.example.commandeservice.dto;

import com.example.commandeservice.model.ModePaiement;
import com.example.commandeservice.model.StatutCommande;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO de réponse pour le dashboard statistiques admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeStatsDTO {

    // --- Totaux généraux ---
    private long totalCommandes;

    // --- Répartition par statut ---
    private Map<StatutCommande, Long> commandesParStatut;

    // --- Volumes temporels ---
    private long commandesAujourdhui;
    private long commandesCetteSemaine;
    private long commandesCeMois;

    // --- Mode de paiement ---
    private Map<ModePaiement, Long> commandesParModePaiement;
    private ModePaiement modePaiementPlusUtilise;
}

package com.example.commandeservice.service;

import com.example.commandeservice.dto.CommandeStatsDTO;
import com.example.commandeservice.model.Commande;
import com.example.commandeservice.model.ModePaiement;
import com.example.commandeservice.model.StatutCommande;
import com.example.commandeservice.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommandeStatsService {

    @Autowired
    private CommandeRepository commandeRepository;

    public CommandeStatsDTO getStats() {
        List<Commande> toutes = commandeRepository.findAll();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime debutJour     = now.toLocalDate().atStartOfDay();
        LocalDateTime debutSemaine  = now.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime debutMois     = now.toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();

        // 1. Total
        long total = toutes.size();

        // 2. Par statut
        Map<StatutCommande, Long> parStatut = Arrays.stream(StatutCommande.values())
                .collect(Collectors.toMap(
                        statut -> statut,
                        statut -> toutes.stream()
                                .filter(c -> statut.equals(c.getStatutCommande()))
                                .count(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        // 3. Volumes temporels
        long auj     = compterDepuis(toutes, debutJour);
        long semaine = compterDepuis(toutes, debutSemaine);
        long mois    = compterDepuis(toutes, debutMois);

        // 4. Par mode de paiement
        Map<ModePaiement, Long> parMode = Arrays.stream(ModePaiement.values())
                .collect(Collectors.toMap(
                        mode -> mode,
                        mode -> toutes.stream()
                                .filter(c -> mode.equals(c.getModePaiement()))
                                .count(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        // 5. Mode de paiement le plus utilisé
        ModePaiement modePlusUtilise = parMode.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return CommandeStatsDTO.builder()
                .totalCommandes(total)
                .commandesParStatut(parStatut)
                .commandesAujourdhui(auj)
                .commandesCetteSemaine(semaine)
                .commandesCeMois(mois)
                .commandesParModePaiement(parMode)
                .modePaiementPlusUtilise(modePlusUtilise)
                .build();
    }

    private long compterDepuis(List<Commande> toutes, LocalDateTime depuis) {
        return toutes.stream()
                .filter(c -> c.getDateCommande() != null && c.getDateCommande().isAfter(depuis))
                .count();
    }
}

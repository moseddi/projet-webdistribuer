package tn.esprit.livraison.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tn.esprit.livraison.entity.Livraison;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    // --- RECHERCHE PAR ADRESSE (Contient le texte, ignore la casse) ---
    List<Livraison> findByAdresseContainingIgnoreCase(@Param("adresse") String adresse);

    // --- RECHERCHE PAR NUMÉRO DE SUIVI (Exact) ---
    List<Livraison> findByNumeroSuivi(@Param("numeroSuivi") String numeroSuivi);

    // --- RECHERCHE PAR TRANSPORTEUR (Ignore la casse) ---
    List<Livraison> findByTransporteurContainingIgnoreCase(@Param("transporteur") String transporteur);

    // --- RECHERCHE PAR TRANSPORTEUR OU NUMÉRO DE SUIVI (Ce que ton Service appelait) ---
    List<Livraison> findByTransporteurContainingIgnoreCaseOrNumeroSuiviContainingIgnoreCase(
            @Param("transporteur") String transporteur,
            @Param("numeroSuivi") String numeroSuivi
    );

    // --- RECHERCHE ENTRE DEUX DATES (Pour le tri chronologique complexe) ---
    List<Livraison> findByDateLivraisonBetween(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );
}
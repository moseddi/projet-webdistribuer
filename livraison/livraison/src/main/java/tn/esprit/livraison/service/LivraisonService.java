package tn.esprit.livraison.service;

import tn.esprit.livraison.dto.LivraisonDTO;
import tn.esprit.livraison.entity.Livraison;
import tn.esprit.livraison.repository.LivraisonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LivraisonService {

    // Création du Logger manuel (remplace @Slf4j)
    private static final Logger log = LoggerFactory.getLogger(LivraisonService.class);

    private final LivraisonRepository livraisonRepository;

    // Constructeur manuel (remplace @RequiredArgsConstructor)
    public LivraisonService(LivraisonRepository livraisonRepository) {
        this.livraisonRepository = livraisonRepository;
    }

    public LivraisonDTO creerLivraison(Livraison livraison) {
        return new LivraisonDTO(livraisonRepository.save(livraison));
    }

    public List<LivraisonDTO> obtenirToutesLesLivraisons() {
        return livraisonRepository.findAll().stream()
                .map(LivraisonDTO::new)
                .collect(Collectors.toList());
    }

    public LivraisonDTO obtenirLivraisonParId(Long id) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));
        return new LivraisonDTO(livraison);
    }

    public LivraisonDTO modifierLivraison(Long id, Livraison detailsLivraison) {
        Livraison livraisonExistante = livraisonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        livraisonExistante.setAdresse(detailsLivraison.getAdresse());
        livraisonExistante.setTransporteur(detailsLivraison.getTransporteur());
        livraisonExistante.setNumeroSuivi(detailsLivraison.getNumeroSuivi());
        livraisonExistante.setTypeProduit(detailsLivraison.getTypeProduit());
        livraisonExistante.setDateLivraison(detailsLivraison.getDateLivraison());
        livraisonExistante.setMontant(detailsLivraison.getMontant());
        livraisonExistante.setTaxLivraison(detailsLivraison.getTaxLivraison());

        return new LivraisonDTO(livraisonRepository.save(livraisonExistante));
    }

    public void supprimerLivraison(Long id) {
        livraisonRepository.deleteById(id);
    }

    public List<LivraisonDTO> rechercherLivraisons(String keyword) {
        return livraisonRepository.findByTransporteurContainingIgnoreCaseOrNumeroSuiviContainingIgnoreCase(keyword, keyword)
                .stream().map(LivraisonDTO::new).collect(Collectors.toList());
    }

    public List<LivraisonDTO> obtenirLivraisonsTriees(String sortBy) {
        return livraisonRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy)).stream()
                .map(LivraisonDTO::new)
                .collect(Collectors.toList());
    }

    public Map<String, List<LivraisonDTO>> regrouperParZone() {
        return livraisonRepository.findAll().stream()
                .map(LivraisonDTO::new)
                .collect(Collectors.groupingBy(LivraisonDTO::getAdresse));
    }

    @Scheduled(fixedRate = 1800000)
    public void verifierAlerteLivraison() {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dans12Heures = maintenant.plusHours(12);

        List<Livraison> alertes = livraisonRepository.findByDateLivraisonBetween(maintenant, dans12Heures);

        if (!alertes.isEmpty()) {
            log.warn("=== ALERTE : {} livraisons prévues dans moins de 12h ===", alertes.size());
            alertes.forEach(l ->
                    log.info("Urgence pour la livraison {} à l'adresse {}", l.getNumeroSuivi(), l.getAdresse())
            );
        }
    }
}
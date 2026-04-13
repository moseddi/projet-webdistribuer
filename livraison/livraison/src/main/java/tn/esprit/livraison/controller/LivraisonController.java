package tn.esprit.livraison.controller;

import tn.esprit.livraison.dto.LivraisonDTO;
import tn.esprit.livraison.entity.Livraison;
import tn.esprit.livraison.service.LivraisonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livraisons")
@RefreshScope  // permet le rafraichissement de la config sans redemarrage
public class LivraisonController {

    private final LivraisonService livraisonService;

    // Injecte le message depuis le Config Server
    @Value("${welcome.message:Message non configure}")
    private String welcomeMessage;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }

    // ===================== ENDPOINT CONFIG SERVER =====================
    @GetMapping("/welcome")
    public String welcome() {
        return welcomeMessage;
    }

    // ===================== CRUD =====================
    @PostMapping
    public ResponseEntity<LivraisonDTO> creer(@RequestBody Livraison livraison) {
        return ResponseEntity.ok(livraisonService.creerLivraison(livraison));
    }

    @GetMapping
    public ResponseEntity<List<LivraisonDTO>> listerToutes() {
        return ResponseEntity.ok(livraisonService.obtenirToutesLesLivraisons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivraisonDTO> obtenirUne(@PathVariable Long id) {
        return ResponseEntity.ok(livraisonService.obtenirLivraisonParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivraisonDTO> modifier(@PathVariable Long id, @RequestBody Livraison livraison) {
        return ResponseEntity.ok(livraisonService.modifierLivraison(id, livraison));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        livraisonService.supprimerLivraison(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== FONCTIONNALITES AVANCEES =====================
    @GetMapping("/recherche")
    public ResponseEntity<List<LivraisonDTO>> rechercher(@RequestParam String keyword) {
        return ResponseEntity.ok(livraisonService.rechercherLivraisons(keyword));
    }

    @GetMapping("/tri")
    public ResponseEntity<List<LivraisonDTO>> trier(@RequestParam(defaultValue = "dateLivraison") String sortBy) {
        return ResponseEntity.ok(livraisonService.obtenirLivraisonsTriees(sortBy));
    }

    @GetMapping("/zones")
    public ResponseEntity<Map<String, List<LivraisonDTO>>> zonesGeographiques() {
        return ResponseEntity.ok(livraisonService.regrouperParZone());
    }
}
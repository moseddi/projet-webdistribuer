package com.example.commandeservice.controller;

import com.example.commandeservice.model.Commande;
import com.example.commandeservice.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;

    @GetMapping
    public List<Commande> getAllCommandes() {
        return commandeService.getAllCommandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable("id") Long id) {
        return commandeService.getCommandeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/nom")
    public List<Commande> searchByNom(@RequestParam String nom) {
        return commandeService.searchByNom(nom);
    }

    @GetMapping("/search/prenom")
    public List<Commande> searchByPrenom(@RequestParam String prenom) {
        return commandeService.searchByPrenom(prenom);
    }

    @GetMapping("/search")
    public List<Commande> search(@RequestParam String query) {
        return commandeService.search(query);
    }

    @PostMapping
    public Commande createCommande(@RequestBody Commande commande) {
        return commandeService.createCommande(commande);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<Commande> updateStatut(@PathVariable("id") Long id,
            @RequestParam("nouveauStatut") com.example.commandeservice.model.StatutCommande nouveauStatut) {
        try {
            return ResponseEntity.ok(commandeService.updateStatut(id, nouveauStatut));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Commande> cancelCommande(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(commandeService.cancelCommande(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Commande> updateCommande(@PathVariable("id") Long id, @RequestBody Commande commandeDetails) {
        try {
            return ResponseEntity.ok(commandeService.updateCommande(id, commandeDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable("id") Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.commandeservice.service;

import com.example.commandeservice.model.Commande;
import com.example.commandeservice.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

    public Optional<Commande> getCommandeById(Long id) {
        return commandeRepository.findById(id);
    }

    public List<Commande> searchByNom(String nom) {
        return commandeRepository.findByNomContainingIgnoreCase(nom);
    }

    public List<Commande> searchByPrenom(String prenom) {
        return commandeRepository.findByPrenomContainingIgnoreCase(prenom);
    }

    public List<Commande> search(String query) {
        return commandeRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(query, query);
    }

    public Commande createCommande(Commande commande) {
        if (commande.getDateCommande() == null) {
            commande.setDateCommande(java.time.LocalDateTime.now());
        }
        commande.setStatutCommande(com.example.commandeservice.model.StatutCommande.EN_ATTENTE);
        return commandeRepository.save(commande);
    }

    public Commande updateStatut(Long id, com.example.commandeservice.model.StatutCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));
        commande.setStatutCommande(nouveauStatut);
        return commandeRepository.save(commande);
    }

    public Commande cancelCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));

        if (commande.getStatutCommande() == com.example.commandeservice.model.StatutCommande.EXPEDIEE ||
                commande.getStatutCommande() == com.example.commandeservice.model.StatutCommande.LIVREE) {
            throw new RuntimeException("Cannot cancel a shipped or delivered command");
        }

        commande.setStatutCommande(com.example.commandeservice.model.StatutCommande.ANNULEE);
        return commandeRepository.save(commande);
    }

    public Commande updateCommande(Long id, Commande commandeDetails) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));

        if (commande.getStatutCommande() != com.example.commandeservice.model.StatutCommande.EN_ATTENTE) {
            throw new RuntimeException("Cannot update command: Only commands in EN_ATTENTE status can be modified.");
        }

        if (commandeDetails.getPrenom() != null)
            commande.setPrenom(commandeDetails.getPrenom());
        if (commandeDetails.getNom() != null)
            commande.setNom(commandeDetails.getNom());
        if (commandeDetails.getAdresse() != null)
            commande.setAdresse(commandeDetails.getAdresse());
        if (commandeDetails.getTelephone() != null)
            commande.setTelephone(commandeDetails.getTelephone());
        if (commandeDetails.getAdresseEmail() != null)
            commande.setAdresseEmail(commandeDetails.getAdresseEmail());
        if (commandeDetails.getModePaiement() != null)
            commande.setModePaiement(commandeDetails.getModePaiement());

        return commandeRepository.save(commande);
    }

    public void deleteCommande(Long id) {
        commandeRepository.deleteById(id);
    }
}

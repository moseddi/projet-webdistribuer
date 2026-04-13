package com.example.commandeservice.repository;

import com.example.commandeservice.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByNomContainingIgnoreCase(String nom);

    List<Commande> findByPrenomContainingIgnoreCase(String prenom);

    List<Commande> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}

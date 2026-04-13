package tn.esprit.livraison.dto;

import tn.esprit.livraison.entity.Livraison;
import tn.esprit.livraison.entity.TypeProduit;
import java.time.LocalDateTime;

public class LivraisonDTO {
    private Long id;
    private String adresse;
    private String transporteur;
    private String numeroSuivi;
    private TypeProduit typeProduit;
    private LocalDateTime dateLivraison;
    private Integer montant;  // Use Integer, not String
    private Integer taxLivraison;  // Use Integer, not String

    public LivraisonDTO() {}

    public LivraisonDTO(Livraison livraison) {
        this.id = livraison.getId();
        this.adresse = livraison.getAdresse();
        this.transporteur = livraison.getTransporteur();
        this.numeroSuivi = livraison.getNumeroSuivi();
        this.typeProduit = livraison.getTypeProduit();
        this.dateLivraison = livraison.getDateLivraison();
        this.montant = livraison.getMontant();
        this.taxLivraison = livraison.getTaxLivraison();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTransporteur() { return transporteur; }
    public void setTransporteur(String transporteur) { this.transporteur = transporteur; }

    public String getNumeroSuivi() { return numeroSuivi; }
    public void setNumeroSuivi(String numeroSuivi) { this.numeroSuivi = numeroSuivi; }

    public TypeProduit getTypeProduit() { return typeProduit; }
    public void setTypeProduit(TypeProduit typeProduit) { this.typeProduit = typeProduit; }

    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }

    public Integer getMontant() { return montant; }
    public void setMontant(Integer montant) { this.montant = montant; }

    public Integer getTaxLivraison() { return taxLivraison; }
    public void setTaxLivraison(Integer taxLivraison) { this.taxLivraison = taxLivraison; }
}
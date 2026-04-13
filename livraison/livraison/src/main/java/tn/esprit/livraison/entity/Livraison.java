package tn.esprit.livraison.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "livraison")
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adresse;
    private String transporteur;
    private String numeroSuivi;

    @Enumerated(EnumType.STRING)
    private TypeProduit typeProduit;

    private LocalDateTime dateLivraison;

    private Integer montant;  // CHANGE from int to Integer
    private Integer taxLivraison;  // CHANGE from int to Integer

    // Constructors
    public Livraison() {}

    public Livraison(Long id, String adresse, String transporteur, String numeroSuivi,
                     TypeProduit typeProduit, LocalDateTime dateLivraison, Integer montant, Integer taxLivraison) {
        this.id = id;
        this.adresse = adresse;
        this.transporteur = transporteur;
        this.numeroSuivi = numeroSuivi;
        this.typeProduit = typeProduit;
        this.dateLivraison = dateLivraison;
        this.montant = montant;
        this.taxLivraison = taxLivraison;
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
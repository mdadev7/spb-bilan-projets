package com.bilanprojets.entity;

/**
 * Météo / situation globale d'un projet pour une année donnée.
 */
public enum SituationProjet {
    EN_BONNE_VOIE("En bonne voie", "Projet conforme aux prévisions de planning et de budget"),
    SOUS_VIGILANCE("Sous vigilance", "Quelques retards ou risques identifiés mais sous contrôle"),
    CRITIQUE("Critique / En retard", "Retard majeur ou blocage technique nécessitant un arbitrage"),
    TERMINE("Terminé", "Objectifs atteints et projet clôturé avec succès"),
    SUSPENDU("Suspendu", "Projet mis en pause temporairement");

    private final String libelle;
    private final String description;

    SituationProjet(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }
}

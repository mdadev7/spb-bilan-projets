package com.bilanprojets.entity;

/**
 * Statut d'avancement d'une fonctionnalité de projet.
 */
public enum StatutFonctionnalite {
    A_FAIRE("À faire"),
    EN_COURS("En cours de développement"),
    EN_RECETTE("En recette / test"),
    TERMINE("Terminé / Déployé"),
    BLOQUE("Bloqué"),
    REPORTE("Reporté"),
    ANNULE("Annulé");

    private final String libelle;

    StatutFonctionnalite(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

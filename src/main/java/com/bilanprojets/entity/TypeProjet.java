package com.bilanprojets.entity;

/**
 * Cycle de vie et typologie d'un projet.
 * Un projet peut être :
 * - NOUVEAU : Nouveau projet initié cette année
 * - PLURIANNUEL : Projet en développement sur plusieurs années
 * - ANCIEN : Ancien projet (en maintenance ou clôturé)
 */
public enum TypeProjet {
    NOUVEAU("Nouveau projet", "Projet initié durant l'exercice actuel"),
    PLURIANNUEL("Projet pluriannuel", "Projet d'envergure en développement sur plusieurs années"),
    ANCIEN("Ancien projet", "Projet historique ou en phase de maintenance/clôture");

    private final String libelle;
    private final String description;

    TypeProjet(String libelle, String description) {
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

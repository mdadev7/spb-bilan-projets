package com.bilanprojets.entity;

/**
 * Niveaux hiérarchiques de la structure organisationnelle (5 paliers normés).
 * Ordre strict : DIRECTION (1) > DIVISION (2) > SERVICE (3) > SECTION (4)
 */
public enum NiveauStructure {
    DIRECTION(1, "Direction Générale / Centrale", "Niveau 1 - Vision 100% Portefeuille"),
    DIVISION(2, "Division / Département", "Niveau 2 - Supervision divisionnaire"),
    SERVICE(3, "Service Opérationnel", "Niveau 3 - Supervision de service"),
    SECTION(4, "Section Spécialisée / Chef de Section", "Niveau 4 - Supervision de section");

    private final int ordre;
    private final String libelle;
    private final String description;

    NiveauStructure(int ordre, String libelle, String description) {
        this.ordre = ordre;
        this.libelle = libelle;
        this.description = description;
    }

    public int getOrdre() {
        return ordre;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuperieurOuEgal(NiveauStructure autre) {
        return this.ordre <= autre.ordre;
    }

    public boolean isInferieurOuEgal(NiveauStructure autre) {
        return this.ordre >= autre.ordre;
    }

    public static NiveauStructure fromOrdre(int ordre) {
        for (NiveauStructure n : values()) {
            if (n.ordre == ordre) return n;
        }
        throw new IllegalArgumentException("Aucun niveau pour l'ordre : " + ordre);
    }
}
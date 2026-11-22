package com.bilanprojets.entity;

/**
 * Rôles et habilitations des utilisateurs dans l'application BilanProjets v2.0.
 * Correspond à la pyramide hiérarchique à 5 niveaux + rôles transverses.
 * 
 * Ordre hiérarchique (descendant) :
 * ROLE_DIRECTION (N1) > ROLE_DIVISION (N2) > ROLE_SERVICE (N3) > 
 * ROLE_SECTION (N4) > ROLE_RESPONSABLE (N5)
 * 
 * Rôles transverses : ROLE_OBSERVATEUR_GENERAL, ROLE_ADMIN
 */
public enum RoleUtilisateur {
    ROLE_ADMIN("Administrateur Système", "Tous les droits techniques : gestion utilisateurs, configuration, audit global", 0),
    
    ROLE_DIRECTION("Direction Générale / Centrale", "Niveau 1 - Vision 100% Portefeuille : supervise toutes les divisions, services, sections et projets", 1),
    ROLE_DIVISION("Division / Département", "Niveau 2 - Supervision divisionnaire : droits sur sa division, ses services, sections et projets", 2),
    ROLE_SERVICE("Service Opérationnel", "Niveau 3 - Supervision de service : droits sur son service, ses sections et projets", 3),
    ROLE_SECTION("Chef de Section / Section Spécialisée", "Niveau 4 - Supervision de section : droits sur sa section et ses projets", 4),
    ROLE_RESPONSABLE("Responsable de Projet", "Niveau 5 - Chef de Projet Métier/IT : gestion de ses projets assignés (CRUD propres, Consultation+Modification affectés)", 5),
    
    ROLE_OBSERVATEUR_GENERAL("Observateur Général / Audit", "Lecture seule transversale sur tout le portefeuille (pas de restriction hiérarchique)", 99);

    private final String libelle;
    private final String description;
    private final int niveauHierarchique; // 0=admin, 1=direction, ..., 5=responsable, 99=transverse

    RoleUtilisateur(String libelle, String description, int niveauHierarchique) {
        this.libelle = libelle;
        this.description = description;
        this.niveauHierarchique = niveauHierarchique;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }

    public int getNiveauHierarchique() {
        return niveauHierarchique;
    }

    public boolean isRoleHierarchique() {
        return niveauHierarchique >= 1 && niveauHierarchique <= 5;
    }

    public boolean isSuperieurOuEgal(RoleUtilisateur autre) {
        if (this == ROLE_ADMIN || autre == ROLE_ADMIN) return true;
        if (this == ROLE_OBSERVATEUR_GENERAL || autre == ROLE_OBSERVATEUR_GENERAL) return false;
        return this.niveauHierarchique <= autre.niveauHierarchique;
    }

    public boolean isInferieurOuEgal(RoleUtilisateur autre) {
        if (this == ROLE_ADMIN || autre == ROLE_ADMIN) return true;
        if (this == ROLE_OBSERVATEUR_GENERAL || autre == ROLE_OBSERVATEUR_GENERAL) return false;
        return this.niveauHierarchique >= autre.niveauHierarchique;
    }

    public static RoleUtilisateur fromNiveauStructure(NiveauStructure niveau) {
        return switch (niveau) {
            case DIRECTION -> ROLE_DIRECTION;
            case DIVISION -> ROLE_DIVISION;
            case SERVICE -> ROLE_SERVICE;
            case SECTION -> ROLE_SECTION;
        };
    }
}
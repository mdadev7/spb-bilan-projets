package com.bilanprojets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un projet (Nouveau, Ancien ou Pluriannuel).
 * Affecté à un Responsable de projet qui a l'habilitation de renseigner les actions et fonctionnalités.
 * 
 * Distinction importante pour les droits CRUD :
 * - createur_id : utilisateur qui a créé le projet (CRUD complet autorisé)
 * - responsable_id : utilisateur affecté au projet (Consultation + Modification, PAS suppression)
 * - structure_id : structure organisationnelle d'appartenance (visibilité hiérarchique)
 */
@Entity
@Table(name = "projet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code projet est obligatoire")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "L'intitulé du projet est obligatoire")
    @Column(name = "intitule", nullable = false, length = 255)
    private String intitule;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Typologie du projet : NOUVEAU, PLURIANNUEL ou ANCIEN
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_projet", nullable = false, length = 50)
    @Builder.Default
    private TypeProjet typeProjet = TypeProjet.PLURIANNUEL;

    @Column(name = "annee_debut")
    private Integer anneeDebut;

    @Column(name = "annee_fin_prevue")
    private Integer anneeFinPrevue;

    /**
     * Responsable du projet affecté (habilité à alimenter les actions, fonctionnalités et bilans)
     * Droits : Consultation + Modification des bilans/actions/fonctionnalités
     * Interdiction : Suppression du projet
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    @JsonIgnoreProperties({"projetsAssignes", "password"})
    private Utilisateur responsable;

    /**
     * Créateur du projet (utilisateur qui a créé l'enregistrement)
     * Droits : CRUD complet (Création, Consultation, Modification, Suppression)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id")
    @JsonIgnoreProperties({"projetsCrees", "password"})
    private Utilisateur createur;

    /**
     * Structure organisationnelle d'appartenance du projet (pour la visibilité hiérarchique)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id")
    @JsonIgnoreProperties({"projets", "utilisateurs", "enfants", "parent"})
    private StructureOrganisationnelle structure;

    /**
     * Indicateur d'activité récente : vrai si action_en_cours ou mise à jour < 7 jours
     */
    @Column(name = "action_en_cours", nullable = false)
    @Builder.Default
    private Boolean actionEnCours = false;

    /**
     * Date de la dernière action/modification significative sur le projet
     * (mise à jour bilan, ajout fonctionnalité, changement statut, etc.)
     */
    @Column(name = "date_derniere_action")
    private LocalDateTime dateDerniereAction;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("projet")
    @OrderBy("annee DESC")
    @Builder.Default
    private List<BilanAnnuel> bilans = new ArrayList<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("projet")
    @Builder.Default
    private List<Fonctionnalite> fonctionnalites = new ArrayList<>();

    // Méthodes d'aide pour les liaisons
    public void addBilan(BilanAnnuel bilan) {
        bilans.add(bilan);
        bilan.setProjet(this);
    }

    public void removeBilan(BilanAnnuel bilan) {
        bilans.remove(bilan);
        bilan.setProjet(null);
    }

    public void addFonctionnalite(Fonctionnalite f) {
        fonctionnalites.add(f);
        f.setProjet(this);
    }

    public void removeFonctionnalite(Fonctionnalite f) {
        fonctionnalites.remove(f);
        f.setProjet(null);
    }

    /**
     * Vérifie si l'utilisateur donné est le créateur du projet (droit de suppression)
     */
    public boolean estCreePar(Utilisateur user) {
        return createur != null && createur.getId().equals(user.getId());
    }

    /**
     * Vérifie si l'utilisateur donné est le responsable affecté du projet
     */
    public boolean estAssigneA(Utilisateur user) {
        return responsable != null && responsable.getId().equals(user.getId());
    }

    /**
     * Vérifie si le projet a eu une action récente (moins de 7 jours)
     */
    public boolean aActionRecente() {
        if (Boolean.TRUE.equals(actionEnCours)) return true;
        if (dateDerniereAction == null) return false;
        return dateDerniereAction.isAfter(LocalDateTime.now().minusDays(7));
    }
}
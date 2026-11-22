package com.bilanprojets.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Utilisateur avec gestion des rôles, structure organisationnelle et habilitations.
 * Supporte l'héritage hiérarchique et les délégations d'intérim.
 */
@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'identifiant (username) est obligatoire")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Le nom complet est obligatoire")
    @Column(name = "nom_complet", nullable = false, length = 150)
    private String nomComplet;

    @Email(message = "Format d'email invalide")
    @Column(name = "email", length = 150)
    private String email;

    @NotNull(message = "Le rôle est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private RoleUtilisateur role;

    /**
     * Structure organisationnelle de rattachement
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_id")
    @JsonIgnoreProperties({"utilisateurs", "enfants", "parent", "projets"})
    private StructureOrganisationnelle structure;

    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    /**
     * Projets dont l'utilisateur est le responsable affecté
     */
    @OneToMany(mappedBy = "responsable", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("responsable")
    @Builder.Default
    private List<Projet> projetsAssignes = new ArrayList<>();

    /**
     * Projets créés par l'utilisateur (pour la règle CRUD propre vs affecté)
     */
    @OneToMany(mappedBy = "createur", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("createur")
    @Builder.Default
    private List<Projet> projetsCrees = new ArrayList<>();

    /**
     * Intérims où l'utilisateur est titulaire (délégant)
     */
    @OneToMany(mappedBy = "titulaire", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("titulaire")
    @Builder.Default
    private List<InterimDelegation> interimsTitulaire = new ArrayList<>();

    /**
     * Intérims où l'utilisateur est intérimaire (bénéficiaire)
     */
    @OneToMany(mappedBy = "interimaire", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("interimaire")
    @Builder.Default
    private List<InterimDelegation> interimsInterimaire = new ArrayList<>();

    /**
     * Vérifie si l'utilisateur a un rôle hiérarchique (N1 à N5)
     */
    public boolean isRoleHierarchique() {
        return role != null && role.isRoleHierarchique();
    }

    /**
     * Vérifie si l'utilisateur est admin
     */
    public boolean isAdmin() {
        return role == RoleUtilisateur.ROLE_ADMIN;
    }

    /**
     * Vérifie si l'utilisateur est observateur général
     */
    public boolean isObservateurGeneral() {
        return role == RoleUtilisateur.ROLE_OBSERVATEUR_GENERAL;
    }

    /**
     * Récupère le niveau hiérarchique numérique (1 à 5, ou 0 pour admin, 99 pour observateur)
     */
    public int getNiveauHierarchique() {
        return role != null ? role.getNiveauHierarchique() : 99;
    }
}
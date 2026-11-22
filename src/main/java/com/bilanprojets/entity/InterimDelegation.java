package com.bilanprojets.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une délégation d'intérim (mandat temporaire).
 * Pendant la période d'intérim active, l'intérimaire hérite de l'ensemble des privilèges,
 * visibilités et prérogatives du titulaire délégué.
 */
@Entity
@Table(name = "interim_delegation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterimDelegation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titulaire du poste (utilisateur qui délègue ses privilèges)
     */
    @NotNull(message = "Le titulaire est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "titulaire_id", nullable = false)
    @JsonIgnoreProperties({"interimsTitulaire", "password"})
    private Utilisateur titulaire;

    /**
     * Intérimaire (bénéficiaire de la délégation)
     */
    @NotNull(message = "L'intérimaire est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "interimaire_id", nullable = false)
    @JsonIgnoreProperties({"interimsInterimaire", "password"})
    private Utilisateur interimaire;

    /**
     * Structure concernée par l'intérim (Division, Service, Section, etc.)
     */
    @NotNull(message = "La structure est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "structure_id", nullable = false)
    @JsonIgnoreProperties("interimDelegations")
    private StructureOrganisationnelle structure;

    /**
     * Niveau délégué (DIRECTION, DIVISION, SERVICE, SECTION)
     */
    @NotNull(message = "Le niveau délégué est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_delegue", nullable = false, length = 20)
    private NiveauStructure niveauDelegue;

    /**
     * Date de début de l'intérim (inclus)
     */
    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    /**
     * Date de fin de l'intérim (inclus)
     */
    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    /**
     * Indicateur d'activation manuelle (peut être activé/désactivé avant la date)
     */
    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "motif", length = 255)
    private String motif;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    /**
     * Vérifie si l'intérim est actif à la date actuelle (en tenant compte de l'indicateur manuel)
     */
    public boolean estActifMaintenant() {
        if (!Boolean.TRUE.equals(this.actif)) {
            return false;
        }
        LocalDate aujourdhui = LocalDate.now();
        return !aujourdhui.isBefore(dateDebut) && !aujourdhui.isAfter(dateFin);
    }

    /**
     * Vérifie si l'intérim est actif à une date donnée
     */
    public boolean estActifA(LocalDate date) {
        if (!Boolean.TRUE.equals(this.actif)) {
            return false;
        }
        return !date.isBefore(dateDebut) && !date.isAfter(dateFin);
    }
}
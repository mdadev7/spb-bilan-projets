package com.bilanprojets.dto;

import com.bilanprojets.entity.SituationProjet;
import com.bilanprojets.entity.TypeProjet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO Fiche Complète : décrit la situation d'un projet pour une année donnée
 * Conforme aux spécifications :
 * - annee
 * - intitulé
 * - description
 * - liste fonctionnalités avec statut de chacune
 * - fonctionnalités principales
 * - actions durant l'année en cours
 * - situation de projet (détails + météo)
 * - perspectives année suivante (X+1)
 * - Responsable affecté & cycle de vie (Nouveau, Pluriannuel, Ancien)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SituationProjetFicheDTO {

    private Long projetId;
    private String codeProjet;

    @NotNull(message = "L'année est obligatoire")
    private Integer annee;

    @NotBlank(message = "L'intitulé du projet est obligatoire")
    private String intitule;

    private String description;

    @Builder.Default
    private TypeProjet typeProjet = TypeProjet.PLURIANNUEL;
    private String typeProjetLibelle;

    private Integer anneeDebut;
    private Integer anneeFinPrevue;

    // Responsable affecté
    private Long responsableId;
    private String responsableNomComplet;
    private String responsableUsername;

    @NotNull(message = "La situation du projet est obligatoire")
    private SituationProjet situationProjet;

    private String situationProjetLibelle;

    /**
     * Actions menées, jalons et réalisations durant l'année (exercice X)
     */
    private String actionsAnneeEnCours;

    /**
     * Détail de la situation et avancement (météo, risques, état d'avancement global)
     * Section "Situation du projet" du rapport Word
     */
    private String situationDetails;

    /**
     * Synthèse des fonctionnalités majeures et prioritaires
     * Section "Fonctionnalités principales" du rapport Word
     */
    private String fonctionnalitesPrincipales;

    /**
     * Actions prévues pour l'année suivante (X+1)
     * Section "Perspectives" du rapport Word
     */
    private String perspectivesAnneeSuivante;

    /**
     * Commentaires libres (ancien champ, conservé pour compatibilité)
     */
    private String commentaires;

    // Statistiques calculées
    private Integer totalFonctionnalites;
    private Integer fonctionnalitesTerminees;
    private Integer fonctionnalitesEnCours;
    private Integer fonctionnalitesBloquees;
    private Double pourcentageAvancement;

    // Liste des fonctionnalités avec statut individuel
    @Builder.Default
    private List<FonctionnaliteDTO> listeFonctionnalites = new ArrayList<>();

    private LocalDateTime dateDerniereMiseAJour;
}
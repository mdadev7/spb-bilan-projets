package com.bilanprojets.dto;

import com.bilanprojets.entity.SituationProjet;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BilanAnnuelDTO {
    private Long id;

    @NotNull(message = "L'ID du projet est obligatoire")
    private Long projetId;

    private String projetIntitule;
    private String projetCode;

    @NotNull(message = "L'année est obligatoire")
    private Integer annee;

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

    private LocalDateTime dateEvaluation;
    private LocalDateTime dateMiseAJour;

    /**
     * Indique si ce projet est sélectionné pour le rapport annuel consolidé.
     */
    private Boolean inclusRapportAnnuel;
}
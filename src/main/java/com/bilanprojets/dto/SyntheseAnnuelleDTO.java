package com.bilanprojets.dto;

import com.bilanprojets.entity.SituationProjet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Synthèse globale du bilan annuel pour l'ensemble des projets d'une année.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyntheseAnnuelleDTO {
    private Integer annee;
    private Integer totalProjets;
    private Map<SituationProjet, Long> repartitionParSituation;
    private Integer totalFonctionnalites;
    private Integer totalFonctionnalitesTerminees;
    private Double tauxMoyenReussite;
    private List<SituationProjetFicheDTO> fichesProjets;
}

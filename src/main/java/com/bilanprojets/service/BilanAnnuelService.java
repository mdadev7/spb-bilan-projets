package com.bilanprojets.service;

import com.bilanprojets.dto.BilanAnnuelDTO;
import com.bilanprojets.dto.SituationProjetFicheDTO;
import com.bilanprojets.dto.SyntheseAnnuelleDTO;
import com.bilanprojets.entity.SituationProjet;

import java.util.List;
import java.util.Map;

public interface BilanAnnuelService {
    BilanAnnuelDTO enregistrerBilan(BilanAnnuelDTO dto);
    BilanAnnuelDTO modifierBilan(Long id, BilanAnnuelDTO dto);
    BilanAnnuelDTO getBilanById(Long id);
    BilanAnnuelDTO getBilanByProjetEtAnnee(Long projetId, Integer annee);
    SituationProjetFicheDTO getSituationProjetFiche(Long projetId, Integer annee);
    SituationProjetFicheDTO enregistrerFicheComplete(SituationProjetFicheDTO ficheDTO);
    List<BilanAnnuelDTO> getBilansByAnnee(Integer annee);
    List<BilanAnnuelDTO> getBilansByProjet(Long projetId);
    List<BilanAnnuelDTO> getBilansByAnneeEtSituation(Integer annee, SituationProjet situation);
    SyntheseAnnuelleDTO getSyntheseAnnuelle(Integer annee);
    void supprimerBilan(Long id);

    // Nouvelles méthodes pour la sélection et l'export (Phase 4 & 5)
    List<BilanAnnuelDTO> getProjetsEligiblesPourRapport(Integer annee);
    String genererApercuHtml(Integer annee, List<Long> projetIds);
    byte[] genererRapportWord(Integer annee, List<Long> projetIds, String titreDocument);
    byte[] genererRapportPdf(Integer annee, List<Long> projetIds, String titreDocument);
    void mettreAJourSelectionRapport(Integer annee, List<Long> projetIdsInclus);
}

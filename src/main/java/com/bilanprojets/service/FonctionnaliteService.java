package com.bilanprojets.service;

import com.bilanprojets.dto.FonctionnaliteDTO;
import com.bilanprojets.entity.StatutFonctionnalite;

import java.util.List;

public interface FonctionnaliteService {
    FonctionnaliteDTO ajouterFonctionnalite(Long projetId, FonctionnaliteDTO dto);
    FonctionnaliteDTO modifierFonctionnalite(Long id, FonctionnaliteDTO dto);
    FonctionnaliteDTO changerStatut(Long id, StatutFonctionnalite nouveauStatut);
    FonctionnaliteDTO getFonctionnaliteById(Long id);
    List<FonctionnaliteDTO> getFonctionnalitesByProjet(Long projetId);
    List<FonctionnaliteDTO> getFonctionnalitesByProjetEtStatut(Long projetId, StatutFonctionnalite statut);
    List<FonctionnaliteDTO> getFonctionnalitesPrincipalesByProjet(Long projetId);
    void supprimerFonctionnalite(Long id);
}

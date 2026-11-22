package com.bilanprojets.service;

import com.bilanprojets.dto.UtilisateurDTO;
import com.bilanprojets.entity.RoleUtilisateur;

import java.util.List;

public interface UtilisateurService {
    UtilisateurDTO creerUtilisateur(UtilisateurDTO dto);
    UtilisateurDTO modifierUtilisateur(Long id, UtilisateurDTO dto);
    UtilisateurDTO getUtilisateurById(Long id);
    UtilisateurDTO getUtilisateurByUsername(String username);
    List<UtilisateurDTO> getAllUtilisateurs();
    List<UtilisateurDTO> getResponsablesProjet();
    void changerStatutActif(Long id, boolean actif);
    void supprimerUtilisateur(Long id);
}

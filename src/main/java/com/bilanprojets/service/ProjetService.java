package com.bilanprojets.service;

import com.bilanprojets.dto.ProjetDTO;
import com.bilanprojets.entity.TypeProjet;

import java.util.List;

public interface ProjetService {
    ProjetDTO creerProjet(ProjetDTO dto);
    ProjetDTO modifierProjet(Long id, ProjetDTO dto);
    ProjetDTO affecterResponsable(Long projetId, Long responsableId);
    ProjetDTO getProjetById(Long id);
    ProjetDTO getProjetByCode(String code);
    List<ProjetDTO> getAllProjets();
    List<ProjetDTO> getProjetsByResponsable(Long responsableId);
    List<ProjetDTO> getProjetsByResponsableUsername(String username);
    List<ProjetDTO> getProjetsByType(TypeProjet typeProjet);
    List<ProjetDTO> searchProjets(String keyword);
    boolean estResponsableOuAdmin(Long projetId, String username);
    void supprimerProjet(Long id);
}

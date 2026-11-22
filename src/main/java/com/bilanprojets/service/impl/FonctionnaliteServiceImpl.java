package com.bilanprojets.service.impl;

import com.bilanprojets.dto.FonctionnaliteDTO;
import com.bilanprojets.entity.BilanAnnuel;
import com.bilanprojets.entity.Fonctionnalite;
import com.bilanprojets.entity.Projet;
import com.bilanprojets.entity.StatutFonctionnalite;
import com.bilanprojets.exception.ResourceNotFoundException;
import com.bilanprojets.repository.BilanAnnuelRepository;
import com.bilanprojets.repository.FonctionnaliteRepository;
import com.bilanprojets.repository.ProjetRepository;
import com.bilanprojets.service.FonctionnaliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FonctionnaliteServiceImpl implements FonctionnaliteService {

    private final FonctionnaliteRepository fonctionnaliteRepository;
    private final ProjetRepository projetRepository;
    private final BilanAnnuelRepository bilanAnnuelRepository;

    @Override
    public FonctionnaliteDTO ajouterFonctionnalite(Long projetId, FonctionnaliteDTO dto) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + projetId));

        BilanAnnuel bilan = null;
        if (dto.getBilanAnnuelId() != null) {
            bilan = bilanAnnuelRepository.findById(dto.getBilanAnnuelId()).orElse(null);
        }

        Fonctionnalite fonctionnalite = Fonctionnalite.builder()
                .projet(projet)
                .bilanAnnuel(bilan)
                .code(dto.getCode())
                .intitule(dto.getIntitule().trim())
                .description(dto.getDescription())
                .statut(dto.getStatut() != null ? dto.getStatut() : StatutFonctionnalite.A_FAIRE)
                .estPrincipale(dto.getEstPrincipale() != null ? dto.getEstPrincipale() : false)
                .priorite(dto.getPriorite() != null ? dto.getPriorite() : "MOYENNE")
                .build();

        Fonctionnalite saved = fonctionnaliteRepository.save(fonctionnalite);
        return mapToDTO(saved);
    }

    @Override
    public FonctionnaliteDTO modifierFonctionnalite(Long id, FonctionnaliteDTO dto) {
        Fonctionnalite f = fonctionnaliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fonctionnalité introuvable avec l'ID : " + id));

        f.setCode(dto.getCode());
        f.setIntitule(dto.getIntitule().trim());
        f.setDescription(dto.getDescription());
        if (dto.getStatut() != null) {
            f.setStatut(dto.getStatut());
        }
        if (dto.getEstPrincipale() != null) {
            f.setEstPrincipale(dto.getEstPrincipale());
        }
        if (dto.getPriorite() != null) {
            f.setPriorite(dto.getPriorite());
        }
        if (dto.getBilanAnnuelId() != null) {
            BilanAnnuel bilan = bilanAnnuelRepository.findById(dto.getBilanAnnuelId()).orElse(null);
            f.setBilanAnnuel(bilan);
        }

        Fonctionnalite updated = fonctionnaliteRepository.save(f);
        return mapToDTO(updated);
    }

    @Override
    public FonctionnaliteDTO changerStatut(Long id, StatutFonctionnalite nouveauStatut) {
        Fonctionnalite f = fonctionnaliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fonctionnalité introuvable avec l'ID : " + id));
        f.setStatut(nouveauStatut);
        Fonctionnalite updated = fonctionnaliteRepository.save(f);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FonctionnaliteDTO getFonctionnaliteById(Long id) {
        Fonctionnalite f = fonctionnaliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fonctionnalité introuvable avec l'ID : " + id));
        return mapToDTO(f);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FonctionnaliteDTO> getFonctionnalitesByProjet(Long projetId) {
        if (!projetRepository.existsById(projetId)) {
            throw new ResourceNotFoundException("Projet introuvable avec l'ID : " + projetId);
        }
        return fonctionnaliteRepository.findByProjetId(projetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FonctionnaliteDTO> getFonctionnalitesByProjetEtStatut(Long projetId, StatutFonctionnalite statut) {
        return fonctionnaliteRepository.findByProjetIdAndStatut(projetId, statut).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FonctionnaliteDTO> getFonctionnalitesPrincipalesByProjet(Long projetId) {
        return fonctionnaliteRepository.findByProjetIdAndEstPrincipaleTrue(projetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void supprimerFonctionnalite(Long id) {
        if (!fonctionnaliteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fonctionnalité introuvable avec l'ID : " + id);
        }
        fonctionnaliteRepository.deleteById(id);
    }

    public FonctionnaliteDTO mapToDTO(Fonctionnalite f) {
        return FonctionnaliteDTO.builder()
                .id(f.getId())
                .projetId(f.getProjet().getId())
                .bilanAnnuelId(f.getBilanAnnuel() != null ? f.getBilanAnnuel().getId() : null)
                .code(f.getCode())
                .intitule(f.getIntitule())
                .description(f.getDescription())
                .statut(f.getStatut())
                .statutLibelle(f.getStatut().getLibelle())
                .estPrincipale(f.getEstPrincipale())
                .priorite(f.getPriorite())
                .dateCreation(f.getDateCreation())
                .dateMiseAJour(f.getDateMiseAJour())
                .build();
    }
}

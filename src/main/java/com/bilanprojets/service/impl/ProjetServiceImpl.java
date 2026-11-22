package com.bilanprojets.service.impl;

import com.bilanprojets.dto.ProjetDTO;
import com.bilanprojets.entity.Projet;
import com.bilanprojets.entity.RoleUtilisateur;
import com.bilanprojets.entity.StructureOrganisationnelle;
import com.bilanprojets.entity.TypeProjet;
import com.bilanprojets.entity.Utilisateur;
import com.bilanprojets.exception.BusinessException;
import com.bilanprojets.exception.ResourceNotFoundException;
import com.bilanprojets.repository.ProjetRepository;
import com.bilanprojets.repository.UtilisateurRepository;
import com.bilanprojets.service.ProjetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetServiceImpl implements ProjetService {

    private final ProjetRepository projetRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public ProjetDTO creerProjet(ProjetDTO dto) {
        if (projetRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("Un projet avec le code '" + dto.getCode() + "' existe déjà.");
        }

        Utilisateur responsable = null;
        if (dto.getResponsableId() != null) {
            responsable = utilisateurRepository.findById(dto.getResponsableId()).orElse(null);
        }

        // Le créateur est déduit du contexte de sécurité (utilisateur connecté)
        // Pour l'instant, on utilise le responsable comme créateur par défaut si non fourni
        Utilisateur createur = null;
        if (dto.getCreateurId() != null) {
            createur = utilisateurRepository.findById(dto.getCreateurId()).orElse(null);
        } else if (responsable != null) {
            createur = responsable; // Par défaut, le responsable est aussi le créateur
        }

        // Structure : utiliser celle du créateur ou du responsable si disponible
        Utilisateur userForStructure = createur != null ? createur : responsable;
        
        Projet projet = Projet.builder()
                .code(dto.getCode().trim().toUpperCase())
                .intitule(dto.getIntitule().trim())
                .description(dto.getDescription())
                .typeProjet(dto.getTypeProjet() != null ? dto.getTypeProjet() : TypeProjet.PLURIANNUEL)
                .anneeDebut(dto.getAnneeDebut())
                .anneeFinPrevue(dto.getAnneeFinPrevue())
                .responsable(responsable)
                .createur(createur)
                .structure(userForStructure != null ? userForStructure.getStructure() : null)
                .build();

        Projet saved = projetRepository.save(projet);
        return mapToDTO(saved);
    }

    @Override
    public ProjetDTO modifierProjet(Long id, ProjetDTO dto) {
        Projet projet = projetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + id));

        if (!projet.getCode().equalsIgnoreCase(dto.getCode()) && projetRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("Un projet avec le code '" + dto.getCode() + "' existe déjà.");
        }

        projet.setCode(dto.getCode().trim().toUpperCase());
        projet.setIntitule(dto.getIntitule().trim());
        projet.setDescription(dto.getDescription());
        if (dto.getTypeProjet() != null) {
            projet.setTypeProjet(dto.getTypeProjet());
        }
        projet.setAnneeDebut(dto.getAnneeDebut());
        projet.setAnneeFinPrevue(dto.getAnneeFinPrevue());

        if (dto.getResponsableId() != null) {
            Utilisateur responsable = utilisateurRepository.findById(dto.getResponsableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Responsable introuvable avec l'ID : " + dto.getResponsableId()));
            projet.setResponsable(responsable);
        }

        Projet updated = projetRepository.save(projet);
        return mapToDTO(updated);
    }

    @Override
    public ProjetDTO affecterResponsable(Long projetId, Long responsableId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + projetId));

        Utilisateur responsable = utilisateurRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable introuvable avec l'ID : " + responsableId));

        projet.setResponsable(responsable);
        Projet updated = projetRepository.save(projet);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetDTO getProjetById(Long id) {
        Projet projet = projetRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec l'ID : " + id));
        return mapToDTO(projet);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetDTO getProjetByCode(String code) {
        Projet projet = projetRepository.findByCode(code.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable avec le code : " + code));
        return mapToDTO(projet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetDTO> getAllProjets() {
        return projetRepository.findAllWithResponsable().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetDTO> getProjetsByResponsable(Long responsableId) {
        return projetRepository.findByResponsableId(responsableId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetDTO> getProjetsByResponsableUsername(String username) {
        return projetRepository.findByResponsableUsername(username).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetDTO> getProjetsByType(TypeProjet typeProjet) {
        return projetRepository.findByTypeProjet(typeProjet).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetDTO> searchProjets(String keyword) {
        return projetRepository.findByIntituleContainingIgnoreCase(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estResponsableOuAdmin(Long projetId, String username) {
        Utilisateur user = utilisateurRepository.findByUsername(username.toLowerCase()).orElse(null);
        if (user == null) {
            return false;
        }
        if (user.getRole() == RoleUtilisateur.ROLE_ADMIN) {
            return true;
        }
        Projet projet = projetRepository.findById(projetId).orElse(null);
        return projet != null && projet.getResponsable() != null && projet.getResponsable().getId().equals(user.getId());
    }

    @Override
    public void supprimerProjet(Long id) {
        if (!projetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Projet introuvable avec l'ID : " + id);
        }
        projetRepository.deleteById(id);
    }

    private ProjetDTO mapToDTO(Projet projet) {
        Utilisateur resp = projet.getResponsable();
        Utilisateur crea = projet.getCreateur();
        StructureOrganisationnelle struct = projet.getStructure();
        
        return ProjetDTO.builder()
                .id(projet.getId())
                .code(projet.getCode())
                .intitule(projet.getIntitule())
                .description(projet.getDescription())
                .typeProjet(projet.getTypeProjet())
                .typeProjetLibelle(projet.getTypeProjet() != null ? projet.getTypeProjet().getLibelle() : "")
                .anneeDebut(projet.getAnneeDebut())
                .anneeFinPrevue(projet.getAnneeFinPrevue())
                .responsableId(resp != null ? resp.getId() : null)
                .responsableNomComplet(resp != null ? resp.getNomComplet() : "Non assigné")
                .responsableUsername(resp != null ? resp.getUsername() : null)
                .createurId(crea != null ? crea.getId() : null)
                .createurNomComplet(crea != null ? crea.getNomComplet() : null)
                .createurUsername(crea != null ? crea.getUsername() : null)
                .structureId(struct != null ? struct.getId() : null)
                .structureCode(struct != null ? struct.getCode() : null)
                .structureLibelle(struct != null ? struct.getLibelle() : null)
                .actionEnCours(projet.getActionEnCours())
                .dateDerniereAction(projet.getDateDerniereAction())
                .dateCreation(projet.getDateCreation())
                .dateMiseAJour(projet.getDateMiseAJour())
                .build();
    }
}

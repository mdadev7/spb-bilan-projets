package com.bilanprojets.service.impl;

import com.bilanprojets.dto.UtilisateurDTO;
import com.bilanprojets.entity.RoleUtilisateur;
import com.bilanprojets.entity.Utilisateur;
import com.bilanprojets.exception.BusinessException;
import com.bilanprojets.exception.ResourceNotFoundException;
import com.bilanprojets.repository.UtilisateurRepository;
import com.bilanprojets.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UtilisateurDTO creerUtilisateur(UtilisateurDTO dto) {
        if (utilisateurRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("L'identifiant '" + dto.getUsername() + "' est déjà utilisé.");
        }

        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank())
                ? dto.getPassword()
                : "user123";

        Utilisateur u = Utilisateur.builder()
                .username(dto.getUsername().trim().toLowerCase())
                .password(passwordEncoder.encode(rawPassword))
                .nomComplet(dto.getNomComplet().trim())
                .email(dto.getEmail())
                .role(dto.getRole() != null ? dto.getRole() : RoleUtilisateur.ROLE_RESPONSABLE)
                .actif(dto.getActif() != null ? dto.getActif() : true)
                .build();

        Utilisateur saved = utilisateurRepository.save(u);
        return mapToDTO(saved);
    }

    @Override
    public UtilisateurDTO modifierUtilisateur(Long id, UtilisateurDTO dto) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID : " + id));

        u.setNomComplet(dto.getNomComplet().trim());
        u.setEmail(dto.getEmail());
        if (dto.getRole() != null) {
            u.setRole(dto.getRole());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Utilisateur updated = utilisateurRepository.save(u);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurById(Long id) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID : " + id));
        return mapToDTO(u);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurDTO getUtilisateurByUsername(String username) {
        Utilisateur u = utilisateurRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + username));
        return mapToDTO(u);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> getResponsablesProjet() {
        return utilisateurRepository.findByRole(RoleUtilisateur.ROLE_RESPONSABLE).stream()
                .filter(Utilisateur::getActif)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void changerStatutActif(Long id, boolean actif) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'ID : " + id));
        u.setActif(actif);
        utilisateurRepository.save(u);
    }

    @Override
    public void supprimerUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur introuvable avec l'ID : " + id);
        }
        utilisateurRepository.deleteById(id);
    }

    private UtilisateurDTO mapToDTO(Utilisateur u) {
        return UtilisateurDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nomComplet(u.getNomComplet())
                .email(u.getEmail())
                .role(u.getRole())
                .roleLibelle(u.getRole().getLibelle())
                .actif(u.getActif())
                .nombreProjetsAssignes(u.getProjetsAssignes() != null ? u.getProjetsAssignes().size() : 0)
                .dateCreation(u.getDateCreation())
                .build();
    }
}

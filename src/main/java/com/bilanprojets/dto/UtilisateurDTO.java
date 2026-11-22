package com.bilanprojets.dto;

import com.bilanprojets.entity.RoleUtilisateur;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UtilisateurDTO {
    private Long id;

    @NotBlank(message = "L'identifiant est obligatoire")
    private String username;

    private String password;

    @NotBlank(message = "Le nom complet est obligatoire")
    private String nomComplet;

    @Email(message = "Format email invalide")
    private String email;

    @NotNull(message = "Le rôle est obligatoire")
    private RoleUtilisateur role;

    private String roleLibelle;

    @Builder.Default
    private Boolean actif = true;

    private Integer nombreProjetsAssignes;
    private LocalDateTime dateCreation;
}

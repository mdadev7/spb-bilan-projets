package com.bilanprojets.dto;

import com.bilanprojets.entity.StatutFonctionnalite;
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
public class FonctionnaliteDTO {
    private Long id;
    private Long projetId;
    private Long bilanAnnuelId;
    private String code;

    @NotBlank(message = "L'intitulé de la fonctionnalité est obligatoire")
    private String intitule;

    private String description;

    @NotNull(message = "Le statut est obligatoire")
    private StatutFonctionnalite statut;

    private String statutLibelle;

    @Builder.Default
    private Boolean estPrincipale = false;

    @Builder.Default
    private String priorite = "MOYENNE";

    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
}

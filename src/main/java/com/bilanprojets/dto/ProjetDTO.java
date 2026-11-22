package com.bilanprojets.dto;

import com.bilanprojets.entity.TypeProjet;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetDTO {
    private Long id;

    @NotBlank(message = "Le code projet est obligatoire")
    private String code;

    @NotBlank(message = "L'intitulé du projet est obligatoire")
    private String intitule;

    private String description;

    @Builder.Default
    private TypeProjet typeProjet = TypeProjet.PLURIANNUEL;

    private String typeProjetLibelle;

    private Integer anneeDebut;
    private Integer anneeFinPrevue;

    // Informations du Responsable affecté (droits : consultation + modification, PAS suppression)
    private Long responsableId;
    private String responsableNomComplet;
    private String responsableUsername;

    // Informations du Créateur (droits : CRUD complet)
    private Long createurId;
    private String createurNomComplet;
    private String createurUsername;

    // Structure organisationnelle d'appartenance (pour visibilité hiérarchique)
    private Long structureId;
    private String structureCode;
    private String structureLibelle;

    // Indicateurs d'activité pour la liste des projets
    private Boolean actionEnCours;
    private LocalDateTime dateDerniereAction;

    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;

    /**
     * Vérifie si le projet a eu une action récente (moins de 7 jours)
     */
    public boolean aActionRecente() {
        if (Boolean.TRUE.equals(actionEnCours)) return true;
        if (dateDerniereAction == null) return false;
        return dateDerniereAction.isAfter(LocalDateTime.now().minusDays(7));
    }
}
package com.bilanprojets.controller;

import com.bilanprojets.dto.BilanAnnuelDTO;
import com.bilanprojets.dto.SituationProjetFicheDTO;
import com.bilanprojets.entity.SituationProjet;
import com.bilanprojets.service.BilanAnnuelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bilans")
@RequiredArgsConstructor
@Tag(name = "Bilans Annuels", description = "Endpoints de gestion des bilans annuels et situations des projets")
public class BilanAnnuelController {

    private final BilanAnnuelService bilanAnnuelService;

    @PostMapping
    @Operation(summary = "Enregistrer le bilan annuel pour un projet")
    public ResponseEntity<BilanAnnuelDTO> enregistrerBilan(@Valid @RequestBody BilanAnnuelDTO dto) {
        return new ResponseEntity<>(bilanAnnuelService.enregistrerBilan(dto), HttpStatus.CREATED);
    }

    @PostMapping("/fiche-complete")
    @Operation(summary = "Enregistrer une fiche annuelle complète d'un projet (Projet, Année, Situation, Actions, Fonctionnalités et Statuts)")
    public ResponseEntity<SituationProjetFicheDTO> enregistrerFicheComplete(@Valid @RequestBody SituationProjetFicheDTO ficheDTO) {
        return new ResponseEntity<>(bilanAnnuelService.enregistrerFicheComplete(ficheDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un bilan annuel")
    public ResponseEntity<BilanAnnuelDTO> modifierBilan(@PathVariable Long id, @Valid @RequestBody BilanAnnuelDTO dto) {
        return ResponseEntity.ok(bilanAnnuelService.modifierBilan(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un bilan annuel par son identifiant")
    public ResponseEntity<BilanAnnuelDTO> getBilanById(@PathVariable Long id) {
        return ResponseEntity.ok(bilanAnnuelService.getBilanById(id));
    }

    @GetMapping("/annee/{annee}")
    @Operation(summary = "Lister tous les bilans pour une année donnée (ex: 2026)")
    public ResponseEntity<List<BilanAnnuelDTO>> getBilansByAnnee(
            @PathVariable Integer annee,
            @RequestParam(required = false) SituationProjet situation) {
        if (situation != null) {
            return ResponseEntity.ok(bilanAnnuelService.getBilansByAnneeEtSituation(annee, situation));
        }
        return ResponseEntity.ok(bilanAnnuelService.getBilansByAnnee(annee));
    }

    @GetMapping("/projet/{projetId}")
    @Operation(summary = "Lister l'historique des bilans annuels d'un projet")
    public ResponseEntity<List<BilanAnnuelDTO>> getBilansByProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(bilanAnnuelService.getBilansByProjet(projetId));
    }

    @GetMapping("/projet/{projetId}/annee/{annee}")
    @Operation(summary = "Obtenir le bilan d'un projet pour une année spécifique")
    public ResponseEntity<BilanAnnuelDTO> getBilanByProjetEtAnnee(
            @PathVariable Long projetId,
            @PathVariable Integer annee) {
        return ResponseEntity.ok(bilanAnnuelService.getBilanByProjetEtAnnee(projetId, annee));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un bilan annuel")
    public ResponseEntity<Void> supprimerBilan(@PathVariable Long id) {
        bilanAnnuelService.supprimerBilan(id);
        return ResponseEntity.noContent().build();
    }
}

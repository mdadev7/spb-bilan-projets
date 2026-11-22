package com.bilanprojets.controller;

import com.bilanprojets.dto.FonctionnaliteDTO;
import com.bilanprojets.dto.ProjetDTO;
import com.bilanprojets.dto.SituationProjetFicheDTO;
import com.bilanprojets.service.BilanAnnuelService;
import com.bilanprojets.service.FonctionnaliteService;
import com.bilanprojets.service.ProjetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
@Tag(name = "Projets", description = "Endpoints de gestion des projets")
public class ProjetController {

    private final ProjetService projetService;
    private final BilanAnnuelService bilanAnnuelService;
    private final FonctionnaliteService fonctionnaliteService;

    @PostMapping
    @Operation(summary = "Créer un nouveau projet")
    public ResponseEntity<ProjetDTO> creerProjet(@Valid @RequestBody ProjetDTO dto) {
        return new ResponseEntity<>(projetService.creerProjet(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister tous les projets")
    public ResponseEntity<List<ProjetDTO>> getAllProjets(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(projetService.searchProjets(search));
        }
        return ResponseEntity.ok(projetService.getAllProjets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'un projet par son ID")
    public ResponseEntity<ProjetDTO> getProjetById(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.getProjetById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Obtenir les détails d'un projet par son code unique")
    public ResponseEntity<ProjetDTO> getProjetByCode(@PathVariable String code) {
        return ResponseEntity.ok(projetService.getProjetByCode(code));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un projet existant")
    public ResponseEntity<ProjetDTO> modifierProjet(@PathVariable Long id, @Valid @RequestBody ProjetDTO dto) {
        return ResponseEntity.ok(projetService.modifierProjet(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un projet")
    public ResponseEntity<Void> supprimerProjet(@PathVariable Long id) {
        projetService.supprimerProjet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/situation/{annee}")
    @Operation(summary = "Obtenir la fiche complète de la situation du projet pour une année donnée (année, intitulé, description, fonctionnalités, actions, situation)")
    public ResponseEntity<SituationProjetFicheDTO> getSituationAnnuelle(
            @PathVariable Long id,
            @PathVariable Integer annee) {
        return ResponseEntity.ok(bilanAnnuelService.getSituationProjetFiche(id, annee));
    }

    @GetMapping("/{id}/fonctionnalites")
    @Operation(summary = "Obtenir toutes les fonctionnalités d'un projet avec leur statut")
    public ResponseEntity<List<FonctionnaliteDTO>> getFonctionnalitesByProjet(@PathVariable Long id) {
        return ResponseEntity.ok(fonctionnaliteService.getFonctionnalitesByProjet(id));
    }

    @PostMapping("/{id}/fonctionnalites")
    @Operation(summary = "Ajouter une fonctionnalité à un projet avec statut initial")
    public ResponseEntity<FonctionnaliteDTO> ajouterFonctionnalite(
            @PathVariable Long id,
            @Valid @RequestBody FonctionnaliteDTO dto) {
        return new ResponseEntity<>(fonctionnaliteService.ajouterFonctionnalite(id, dto), HttpStatus.CREATED);
    }
}

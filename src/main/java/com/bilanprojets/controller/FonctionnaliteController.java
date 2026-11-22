package com.bilanprojets.controller;

import com.bilanprojets.dto.FonctionnaliteDTO;
import com.bilanprojets.entity.StatutFonctionnalite;
import com.bilanprojets.service.FonctionnaliteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fonctionnalites")
@RequiredArgsConstructor
@Tag(name = "Fonctionnalités", description = "Endpoints de gestion unitaire des fonctionnalités et mise à jour de leur statut")
public class FonctionnaliteController {

    private final FonctionnaliteService fonctionnaliteService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir les détails d'une fonctionnalité")
    public ResponseEntity<FonctionnaliteDTO> getFonctionnaliteById(@PathVariable Long id) {
        return ResponseEntity.ok(fonctionnaliteService.getFonctionnaliteById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une fonctionnalité existante")
    public ResponseEntity<FonctionnaliteDTO> modifierFonctionnalite(
            @PathVariable Long id,
            @Valid @RequestBody FonctionnaliteDTO dto) {
        return ResponseEntity.ok(fonctionnaliteService.modifierFonctionnalite(id, dto));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour rapidement le statut d'une fonctionnalité (A_FAIRE, EN_COURS, EN_RECETTE, TERMINE, BLOQUE, ANNULE)")
    public ResponseEntity<FonctionnaliteDTO> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutFonctionnalite statut) {
        return ResponseEntity.ok(fonctionnaliteService.changerStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une fonctionnalité")
    public ResponseEntity<Void> supprimerFonctionnalite(@PathVariable Long id) {
        fonctionnaliteService.supprimerFonctionnalite(id);
        return ResponseEntity.noContent().build();
    }
}

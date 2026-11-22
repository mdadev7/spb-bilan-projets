package com.bilanprojets.controller;

import com.bilanprojets.dto.BilanAnnuelDTO;
import com.bilanprojets.dto.SyntheseAnnuelleDTO;
import com.bilanprojets.service.BilanAnnuelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/synthese")
@RequiredArgsConstructor
@Tag(name = "Synthèse & Tableau de bord", description = "Synthèse globale annuelle des bilans et situations de tous les projets")
public class SyntheseAnnuelleController {

    private final BilanAnnuelService bilanAnnuelService;

    @GetMapping("/annee/{annee}")
    @Operation(summary = "Obtenir le tableau de bord et la synthèse globale pour une année donnée")
    public ResponseEntity<SyntheseAnnuelleDTO> getSyntheseAnnuelle(@PathVariable Integer annee) {
        return ResponseEntity.ok(bilanAnnuelService.getSyntheseAnnuelle(annee));
    }

    @GetMapping("/annee/{annee}/projets-eligibles")
    @Operation(summary = "Obtenir la liste des projets éligibles pour le rapport annuel (avec état de sélection)")
    public ResponseEntity<List<BilanAnnuelDTO>> getProjetsEligibles(@PathVariable Integer annee) {
        return ResponseEntity.ok(bilanAnnuelService.getProjetsEligiblesPourRapport(annee));
    }

    @PostMapping("/annee/{annee}/preview")
    @Operation(summary = "Générer l'aperçu HTML du rapport pour les projets sélectionnés")
    public ResponseEntity<String> genererApercu(
            @PathVariable Integer annee,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> projetIds = request.get("projetIds");
        if (projetIds == null || projetIds.isEmpty()) {
            return ResponseEntity.badRequest().body("<div class=\"alert alert-danger\">Aucun projet sélectionné pour l'aperçu.</div>");
        }
        String html = bilanAnnuelService.genererApercuHtml(annee, projetIds);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @PostMapping("/annee/{annee}/export-word")
    @Operation(summary = "Générer et télécharger le rapport Word (.docx) pour les projets sélectionnés")
    public ResponseEntity<byte[]> exportWord(
            @PathVariable Integer annee,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> projetIds = request.get("projetIds");
        if (projetIds == null || projetIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String titre = "Bilan Annuel des Projets " + annee;
        byte[] document = bilanAnnuelService.genererRapportWord(annee, projetIds, titre);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDispositionFormData("attachment", "Bilan_Annuel_Projets_" + annee + ".docx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(document);
    }

    @PostMapping("/annee/{annee}/export-pdf")
    @Operation(summary = "Générer et télécharger le rapport PDF pour les projets sélectionnés")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable Integer annee,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> projetIds = request.get("projetIds");
        if (projetIds == null || projetIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String titre = "Bilan Annuel des Projets " + annee;
        byte[] document = bilanAnnuelService.genererRapportPdf(annee, projetIds, titre);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Bilan_Annuel_Projets_" + annee + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(document);
    }

    @PostMapping("/annee/{annee}/selection")
    @Operation(summary = "Mettre à jour la sélection des projets pour le rapport annuel")
    public ResponseEntity<Void> mettreAJourSelection(
            @PathVariable Integer annee,
            @RequestBody Map<String, List<Long>> request) {
        List<Long> projetIdsInclus = request.get("projetIds");
        bilanAnnuelService.mettreAJourSelectionRapport(annee, projetIdsInclus != null ? projetIdsInclus : List.of());
        return ResponseEntity.ok().build();
    }
}
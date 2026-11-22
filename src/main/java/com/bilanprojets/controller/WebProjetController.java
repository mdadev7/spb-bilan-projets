package com.bilanprojets.controller;

import com.bilanprojets.dto.BilanAnnuelDTO;
import com.bilanprojets.dto.FonctionnaliteDTO;
import com.bilanprojets.dto.ProjetDTO;
import com.bilanprojets.dto.SituationProjetFicheDTO;
import com.bilanprojets.dto.SyntheseAnnuelleDTO;
import com.bilanprojets.entity.SituationProjet;
import com.bilanprojets.entity.StatutFonctionnalite;
import com.bilanprojets.entity.TypeProjet;
import com.bilanprojets.service.BilanAnnuelService;
import com.bilanprojets.service.FonctionnaliteService;
import com.bilanprojets.service.ProjetService;
import com.bilanprojets.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.List;

/**
 * Utilitaire pour extraire le message d'erreur racine d'une exception.
 * Utile pour les erreurs de commit JPA qui wrappent la vraie cause.
 */
class ErrorUtils {
    static String getRootCauseMessage(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }
}

@Controller
@RequiredArgsConstructor
public class WebProjetController {

    private final ProjetService projetService;
    private final BilanAnnuelService bilanAnnuelService;
    private final FonctionnaliteService fonctionnaliteService;
    private final UtilisateurService utilisateurService;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(value = "annee", required = false) Integer annee,
            Model model,
            Authentication auth) {
        int anneeCourante = (annee != null) ? annee : Year.now().getValue();
        SyntheseAnnuelleDTO synthese = bilanAnnuelService.getSyntheseAnnuelle(anneeCourante);
        List<ProjetDTO> tousProjets = projetService.getAllProjets();

        model.addAttribute("anneeCourante", anneeCourante);
        model.addAttribute("synthese", synthese);
        model.addAttribute("totalProjets", tousProjets.size());
        model.addAttribute("username", auth != null ? auth.getName() : "");
        return "dashboard";
    }

    @GetMapping("/projets")
    public String listerProjets(
            @RequestParam(value = "type", required = false) TypeProjet type,
            @RequestParam(value = "mesProjets", required = false) Boolean mesProjets,
            Model model,
            Authentication auth) {

        List<ProjetDTO> projets;
        if (Boolean.TRUE.equals(mesProjets) && auth != null) {
            projets = projetService.getProjetsByResponsableUsername(auth.getName());
        } else if (type != null) {
            projets = projetService.getProjetsByType(type);
        } else {
            projets = projetService.getAllProjets();
        }

        model.addAttribute("projets", projets);
        model.addAttribute("filtreType", type);
        model.addAttribute("filtreMesProjets", mesProjets);
        return "projets/liste";
    }

    @GetMapping("/projets/{id}")
    public String detailsProjet(
            @PathVariable Long id,
            @RequestParam(value = "annee", required = false) Integer annee,
            Model model,
            Authentication auth) {

        int anneeSelect = (annee != null) ? annee : Year.now().getValue();
        ProjetDTO projet = projetService.getProjetById(id);
        SituationProjetFicheDTO fiche = bilanAnnuelService.getSituationProjetFiche(id, anneeSelect);
        List<BilanAnnuelDTO> historiqueBilans = bilanAnnuelService.getBilansByProjet(id);

        boolean peutModifier = auth != null && (
                auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) ||
                (projet.getResponsableUsername() != null && projet.getResponsableUsername().equalsIgnoreCase(auth.getName()))
        );

        model.addAttribute("projet", projet);
        model.addAttribute("fiche", fiche);
        model.addAttribute("historiqueBilans", historiqueBilans);
        model.addAttribute("anneeSelect", anneeSelect);
        model.addAttribute("peutModifier", peutModifier);
        model.addAttribute("situations", SituationProjet.values());
        model.addAttribute("statuts", StatutFonctionnalite.values());
        model.addAttribute("responsables", utilisateurService.getResponsablesProjet());
        return "projets/details";
    }

    @GetMapping("/projets/nouveau")
    public String formNouveauProjet(Model model) {
        model.addAttribute("projet", new ProjetDTO());
        model.addAttribute("typesProjet", TypeProjet.values());
        model.addAttribute("responsables", utilisateurService.getResponsablesProjet());
        return "projets/form";
    }

    @PostMapping("/projets/creer")
    public String creerProjet(
            @ModelAttribute ProjetDTO dto,
            RedirectAttributes redirect) {
        try {
            ProjetDTO saved = projetService.creerProjet(dto);
            redirect.addFlashAttribute("successMessage", "Le projet '" + saved.getIntitule() + "' a été créé et affecté avec succès !");
            return "redirect:/projets/" + saved.getId();
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/projets/nouveau";
        }
    }

    @GetMapping("/projets/{id}/modifier")
    public String formModifierProjet(@PathVariable Long id, Model model) {
        ProjetDTO projet = projetService.getProjetById(id);
        model.addAttribute("projet", projet);
        model.addAttribute("typesProjet", TypeProjet.values());
        model.addAttribute("responsables", utilisateurService.getResponsablesProjet());
        return "projets/form";
    }

    @PostMapping("/projets/{id}/modifier")
    public String modifierProjet(
            @PathVariable Long id,
            @ModelAttribute ProjetDTO dto,
            RedirectAttributes redirect) {
        try {
            projetService.modifierProjet(id, dto);
            redirect.addFlashAttribute("successMessage", "Projet mis à jour avec succès !");
        } catch (Exception e) {
            String rootMessage = ErrorUtils.getRootCauseMessage(e);
            redirect.addFlashAttribute("errorMessage", "Erreur : " + rootMessage);
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/projets/{id}/affecter")
    public String affecterResponsable(
            @PathVariable Long id,
            @RequestParam Long responsableId,
            RedirectAttributes redirect) {
        try {
            projetService.affecterResponsable(id, responsableId);
            redirect.addFlashAttribute("successMessage", "Le responsable du projet a été mis à jour !");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/projets/{id}/bilans")
    public String alimenterBilan(
            @PathVariable Long id,
            @RequestParam Integer annee,
            @RequestParam SituationProjet situationProjet,
            @RequestParam(required = false) String actionsAnneeEnCours,
            @RequestParam(required = false) String fonctionnalitesPrincipales,
            @RequestParam(required = false) String commentaires,
            Authentication auth,
            RedirectAttributes redirect) {

        if (!projetService.estResponsableOuAdmin(id, auth.getName())) {
            redirect.addFlashAttribute("errorMessage", "Vous n'avez pas l'habilitation nécessaire pour modifier ce projet.");
            return "redirect:/projets/" + id;
        }

        try {
            SituationProjetFicheDTO fiche = SituationProjetFicheDTO.builder()
                    .projetId(id)
                    .annee(annee)
                    .situationProjet(situationProjet)
                    .actionsAnneeEnCours(actionsAnneeEnCours)
                    .fonctionnalitesPrincipales(fonctionnalitesPrincipales)
                    .commentaires(commentaires)
                    .build();

            bilanAnnuelService.enregistrerFicheComplete(fiche);
            redirect.addFlashAttribute("successMessage", "Le bilan pour l'année " + annee + " a été enregistré avec succès !");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/projets/" + id + "?annee=" + annee;
    }

    @PostMapping("/projets/{id}/fonctionnalites")
    public String ajouterFonctionnalite(
            @PathVariable Long id,
            @ModelAttribute FonctionnaliteDTO dto,
            Authentication auth,
            RedirectAttributes redirect) {

        if (!projetService.estResponsableOuAdmin(id, auth.getName())) {
            redirect.addFlashAttribute("errorMessage", "Habilitation insuffisante : seul le responsable affecté ou l'admin peut ajouter des fonctionnalités.");
            return "redirect:/projets/" + id;
        }

        try {
            fonctionnaliteService.ajouterFonctionnalite(id, dto);
            redirect.addFlashAttribute("successMessage", "Fonctionnalité ajoutée avec succès !");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur lors de l'ajout : " + e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/projets/{id}/fonctionnalites/{fId}/statut")
    public String changerStatutFonctionnalite(
            @PathVariable Long id,
            @PathVariable Long fId,
            @RequestParam StatutFonctionnalite statut,
            Authentication auth,
            RedirectAttributes redirect) {

        if (!projetService.estResponsableOuAdmin(id, auth.getName())) {
            redirect.addFlashAttribute("errorMessage", "Habilitation insuffisante pour modifier le statut.");
            return "redirect:/projets/" + id;
        }

        try {
            fonctionnaliteService.changerStatut(fId, statut);
            redirect.addFlashAttribute("successMessage", "Statut de la fonctionnalité mis à jour !");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/projets/{id}/fonctionnalites/{fId}/supprimer")
    public String supprimerFonctionnalite(
            @PathVariable Long id,
            @PathVariable Long fId,
            Authentication auth,
            RedirectAttributes redirect) {

        if (!projetService.estResponsableOuAdmin(id, auth.getName())) {
            redirect.addFlashAttribute("errorMessage", "Habilitation insuffisante pour supprimer cette fonctionnalité.");
            return "redirect:/projets/" + id;
        }

        try {
            fonctionnaliteService.supprimerFonctionnalite(fId);
            redirect.addFlashAttribute("successMessage", "Fonctionnalité supprimée.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/projets/" + id;
    }

    @PostMapping("/projets/{id}/supprimer")
    public String supprimerProjet(
            @PathVariable Long id,
            RedirectAttributes redirect) {
        try {
            projetService.supprimerProjet(id);
            redirect.addFlashAttribute("successMessage", "Projet supprimé.");
            return "redirect:/projets";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
            return "redirect:/projets/" + id;
        }
    }

    @GetMapping("/synthese")
    public String syntheseAnnuelle(
            @RequestParam(value = "annee", required = false) Integer annee,
            Model model) {
        int anneeSelect = (annee != null) ? annee : Year.now().getValue();
        SyntheseAnnuelleDTO synthese = bilanAnnuelService.getSyntheseAnnuelle(anneeSelect);
        model.addAttribute("synthese", synthese);
        model.addAttribute("anneeSelect", anneeSelect);
        return "synthese/annuelle";
    }
}

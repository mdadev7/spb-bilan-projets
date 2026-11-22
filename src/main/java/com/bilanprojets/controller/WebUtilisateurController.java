package com.bilanprojets.controller;

import com.bilanprojets.dto.UtilisateurDTO;
import com.bilanprojets.entity.RoleUtilisateur;
import com.bilanprojets.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class WebUtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    public String listerUtilisateurs(Model model) {
        model.addAttribute("utilisateurs", utilisateurService.getAllUtilisateurs());
        model.addAttribute("nouveauUser", new UtilisateurDTO());
        model.addAttribute("roles", RoleUtilisateur.values());
        return "utilisateurs/liste";
    }

    @PostMapping("/creer")
    public String creerUtilisateur(
            @ModelAttribute UtilisateurDTO dto,
            RedirectAttributes redirect) {
        try {
            utilisateurService.creerUtilisateur(dto);
            redirect.addFlashAttribute("successMessage", "Utilisateur '" + dto.getUsername() + "' créé avec succès !");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/utilisateurs";
    }

    @PostMapping("/{id}/statut")
    public String changerStatut(
            @PathVariable Long id,
            @RequestParam boolean actif,
            RedirectAttributes redirect) {
        try {
            utilisateurService.changerStatutActif(id, actif);
            redirect.addFlashAttribute("successMessage", "Statut du compte mis à jour !");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }
        return "redirect:/utilisateurs";
    }
}

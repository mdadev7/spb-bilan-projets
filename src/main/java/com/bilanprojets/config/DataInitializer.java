package com.bilanprojets.config;

import com.bilanprojets.entity.*;
import com.bilanprojets.repository.BilanAnnuelRepository;
import com.bilanprojets.repository.FonctionnaliteRepository;
import com.bilanprojets.repository.ProjetRepository;
import com.bilanprojets.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final ProjetRepository projetRepository;
    private final BilanAnnuelRepository bilanAnnuelRepository;
    private final FonctionnaliteRepository fonctionnaliteRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.data.initialize-demo:true}")
    private boolean initializeDemo;

    @Override
    public void run(String... args) {
        if (!initializeDemo) {
            return;
        }

        if (utilisateurRepository.count() > 0) {
            log.info("Des utilisateurs existent déjà en base MySQL. Initialisation ignorée.");
            return;
        }

        log.info("Initialisation des rôles, utilisateurs et projets de démonstration...");

        // 1. Création des utilisateurs avec rôles distincts
        Utilisateur admin = Utilisateur.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .nomComplet("Alexandre Directeur (Admin)")
                .email("admin@entreprise.com")
                .role(RoleUtilisateur.ROLE_ADMIN)
                .actif(true)
                .build();
        admin = utilisateurRepository.save(admin);

        Utilisateur resp1 = Utilisateur.builder()
                .username("cdupont")
                .password(passwordEncoder.encode("user123"))
                .nomComplet("Claire Dupont (Chef de Projet)")
                .email("claire.dupont@entreprise.com")
                .role(RoleUtilisateur.ROLE_RESPONSABLE)
                .actif(true)
                .build();
        resp1 = utilisateurRepository.save(resp1);

        Utilisateur resp2 = Utilisateur.builder()
                .username("mlambert")
                .password(passwordEncoder.encode("user123"))
                .nomComplet("Marc Lambert (Chef de Projet)")
                .email("marc.lambert@entreprise.com")
                .role(RoleUtilisateur.ROLE_RESPONSABLE)
                .actif(true)
                .build();
        resp2 = utilisateurRepository.save(resp2);

        Utilisateur observateur = Utilisateur.builder()
                .username("lecteur")
                .password(passwordEncoder.encode("user123"))
                .nomComplet("Sophie Bernard (Observatrice)")
                .email("sophie.bernard@entreprise.com")
                .role(RoleUtilisateur.ROLE_OBSERVATEUR_GENERAL)
                .actif(true)
                .build();
        utilisateurRepository.save(observateur);

        // 2. Projet 1 : PLURIANNUEL affecté à Claire Dupont
        Projet projet1 = Projet.builder()
                .code("PRJ-PORTAIL-CLIENT")
                .intitule("Portail E-Commerce & Service Client")
                .description("Refonte globale de l'espace client en ligne et self-service avec suivi temps réel.")
                .typeProjet(TypeProjet.PLURIANNUEL)
                .anneeDebut(2024)
                .anneeFinPrevue(2027)
                .responsable(resp1)
                .build();
        projet1 = projetRepository.save(projet1);

        BilanAnnuel bilan1 = BilanAnnuel.builder()
                .projet(projet1)
                .annee(2026)
                .situationProjet(SituationProjet.EN_BONNE_VOIE)
                .actionsAnneeEnCours("Déploiement passerelle paiement v2, migration Kubernetes, formation support client.")
                .fonctionnalitesPrincipales("SSO OAuth2, Paiement multi-devises, Dashboard client interactif.")
                .commentaires("Excellente adhésion des utilisateurs, calendrier respecté.")
                .build();
        bilan1 = bilanAnnuelRepository.save(bilan1);

        fonctionnaliteRepository.save(Fonctionnalite.builder()
                .projet(projet1).bilanAnnuel(bilan1).code("FCT-AUTH-SSO")
                .intitule("Authentification SSO et gestion du profil client")
                .description("Connexion sécurisée via Google/Microsoft et conformité RGPD.")
                .statut(StatutFonctionnalite.TERMINE).estPrincipale(true).priorite("HAUTE").build());

        fonctionnaliteRepository.save(Fonctionnalite.builder()
                .projet(projet1).bilanAnnuel(bilan1).code("FCT-PAY-MULTI")
                .intitule("Passerelle de paiement Stripe & PayPal")
                .description("Gestion multi-devises avec authentification forte 3D-Secure v2.")
                .statut(StatutFonctionnalite.TERMINE).estPrincipale(true).priorite("CRITIQUE").build());

        fonctionnaliteRepository.save(Fonctionnalite.builder()
                .projet(projet1).bilanAnnuel(bilan1).code("FCT-DASH-CLI")
                .intitule("Tableau de bord de suivi des commandes")
                .description("Visualisation du statut de livraison en temps réel avec géolocalisation.")
                .statut(StatutFonctionnalite.EN_COURS).estPrincipale(true).priorite("HAUTE").build());

        // 3. Projet 2 : PLURIANNUEL affecté à Marc Lambert
        Projet projet2 = Projet.builder()
                .code("PRJ-ERP-LOGISTIQUE")
                .intitule("Modernisation ERP Gestion des Stocks")
                .description("Digitalisation de la chaîne logistique et optimisation des flux entrepôts.")
                .typeProjet(TypeProjet.PLURIANNUEL)
                .anneeDebut(2025)
                .anneeFinPrevue(2028)
                .responsable(resp2)
                .build();
        projet2 = projetRepository.save(projet2);

        BilanAnnuel bilan2 = BilanAnnuel.builder()
                .projet(projet2)
                .annee(2026)
                .situationProjet(SituationProjet.SOUS_VIGILANCE)
                .actionsAnneeEnCours("Audit d'infrastructure entrepôt, intégration lecteurs industriels, résolutions verrous sync.")
                .fonctionnalitesPrincipales("Traçabilité RFID, Algorithme rupture stock, Application mobile scan.")
                .commentaires("Retard de 3 semaines lié aux approvisionnements de terminaux. Plan de rattrapage actif.")
                .build();
        bilan2 = bilanAnnuelRepository.save(bilan2);

        fonctionnaliteRepository.save(Fonctionnalite.builder()
                .projet(projet2).bilanAnnuel(bilan2).code("FCT-TRACE-RFID")
                .intitule("Traçabilité des palettes par puces RFID")
                .description("Scan automatique des entrées et sorties d'entrepôt.")
                .statut(StatutFonctionnalite.EN_COURS).estPrincipale(true).priorite("CRITIQUE").build());

        fonctionnaliteRepository.save(Fonctionnalite.builder()
                .projet(projet2).bilanAnnuel(bilan2).code("FCT-APP-MOBIL")
                .intitule("Application tablette magasinier hors-ligne")
                .description("Mode déconnecté avec synchronisation automatique dès retour réseau.")
                .statut(StatutFonctionnalite.BLOQUE).estPrincipale(true).priorite("HAUTE").build());

        // 4. Projet 3 : NOUVEAU projet affecté à Claire Dupont
        Projet projet3 = Projet.builder()
                .code("PRJ-NOUVEAU-IA")
                .intitule("Assistant IA Support Utilisateur")
                .description("Chatbot génératif basé sur LLM pour l'assistance niveau 1 des collaborateurs internes.")
                .typeProjet(TypeProjet.NOUVEAU)
                .anneeDebut(2026)
                .anneeFinPrevue(2026)
                .responsable(resp1)
                .build();
        projet3 = projetRepository.save(projet3);

        BilanAnnuel bilan3 = BilanAnnuel.builder()
                .projet(projet3)
                .annee(2026)
                .situationProjet(SituationProjet.EN_BONNE_VOIE)
                .actionsAnneeEnCours("Cadrage du projet, benchmark des modèles IA open source, POC validé par les équipes.")
                .fonctionnalitesPrincipales("Indexation de la base documentaire RH, Réponse instantanée aux questions fréquentes.")
                .commentaires("Projet démarré au T1 2026, retours très positifs sur le prototype.")
                .build();
        bilanAnnuelRepository.save(bilan3);

        fonctionnaliteRepository.save(Fonctionnalite.builder()
                .projet(projet3).bilanAnnuel(bilan3).code("FCT-IA-RAG")
                .intitule("Moteur RAG de recherche documentaire")
                .description("Recherche sémantique sur les guides et procédures internes.")
                .statut(StatutFonctionnalite.EN_RECETTE).estPrincipale(true).priorite("HAUTE").build());

        // 5. Projet 4 : ANCIEN projet (Maintenance / Clôturé)
        Projet projet4 = Projet.builder()
                .code("PRJ-LEGACY-COMPTA")
                .intitule("Système Comptable Historique v1")
                .description("Ancien logiciel de comptabilité générale maintenu en conditions opérationnelles.")
                .typeProjet(TypeProjet.ANCIEN)
                .anneeDebut(2019)
                .anneeFinPrevue(2023)
                .responsable(resp2)
                .build();
        projet4 = projetRepository.save(projet4);

        BilanAnnuel bilan4 = BilanAnnuel.builder()
                .projet(projet4)
                .annee(2026)
                .situationProjet(SituationProjet.TERMINE)
                .actionsAnneeEnCours("Application des correctifs fiscaux annuels, archivage des écritures comptables antérieures.")
                .fonctionnalitesPrincipales("Export fiscal FEC, Clôture d'exercice automatisée.")
                .commentaires("Projet en mode maintien uniquement. Remplacement prévu à moyen terme.")
                .build();
        bilanAnnuelRepository.save(bilan4);

        log.info("Données et utilisateurs initialisés avec succès !");
    }
}

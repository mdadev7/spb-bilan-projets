-- ==============================================================================
-- Données de test et d'exemple avec Utilisateurs, Rôles, Structures, Intérims et Projets
-- Version 2.0 : Hiérarchie, Intérims, Bilan enrichi
-- Syntaxe Oracle 19c/21c+
-- ==============================================================================

-- Note : Les tables sont déjà créées par schema.sql (avec DROP CASCADE CONSTRAINTS)
-- Pas besoin de ON DUPLICATE KEY UPDATE car les tables sont vides

-- =============================================================================
-- 1. STRUCTURES ORGANISATIONNELLES (Pyramide 5 niveaux)
-- =============================================================================
-- Niveau 1 : DIRECTION
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(1, 'DG', 'Direction Générale', 'DIRECTION', NULL, 1);

-- Niveau 2 : DIVISIONS
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(2, 'DSI', 'Division Systèmes d''Information', 'DIVISION', 1, 1);
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(3, 'DORG', 'Division Organisation & Processus', 'DIVISION', 1, 1);

-- Niveau 3 : SERVICES
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(4, 'SED', 'Service Études & Développement', 'SERVICE', 2, 1);
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(5, 'SIN', 'Service Infrastructure & Réseaux', 'SERVICE', 2, 1);
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(6, 'SPRO', 'Service Processus Métier', 'SERVICE', 3, 1);

-- Niveau 4 : SECTIONS
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(7, 'SWB', 'Section Web & Mobile', 'SECTION', 4, 1);
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(8, 'SDEC', 'Section Décisionnel & Data', 'SECTION', 4, 1);
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(9, 'SINFRA', 'Section Infrastructure Cloud', 'SECTION', 5, 1);
INSERT INTO structure_organisationnelle (id, code, libelle, niveau, parent_id, actif) VALUES
(10, 'SPROC', 'Section Processus & Qualité', 'SECTION', 6, 1);

-- =============================================================================
-- 2. UTILISATEURS (avec structure et rôles hiérarchiques)
-- Mots de passe hashés par défaut (admin123 pour tous)
-- Hash BCrypt : $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi = "admin123"
-- =============================================================================
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Alexandre Directeur', 'admin@entreprise.com', 'ROLE_ADMIN', 1, 1);

-- Niveau 1 : DIRECTION
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(2, 'dirgen', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Marie Direction', 'marie.dir@entreprise.com', 'ROLE_DIRECTION', 1, 1);

-- Niveau 2 : DIVISION
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(3, 'divsi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Pierre Division SI', 'pierre.divsi@entreprise.com', 'ROLE_DIVISION', 2, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(4, 'divorg', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Sophie Division Org', 'sophie.divorg@entreprise.com', 'ROLE_DIVISION', 3, 1);

-- Niveau 3 : SERVICE
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(5, 'svcdev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Thomas Service Dev', 'thomas.svcdev@entreprise.com', 'ROLE_SERVICE', 4, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(6, 'svcinf', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Julie Service Infra', 'julie.svcinf@entreprise.com', 'ROLE_SERVICE', 5, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(7, 'svcpro', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Marc Service Processus', 'marc.svcpro@entreprise.com', 'ROLE_SERVICE', 6, 1);

-- Niveau 4 : SECTION (Chefs de section)
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(8, 'chefweb', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Claire Chef Web', 'claire.chefweb@entreprise.com', 'ROLE_SECTION', 7, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(9, 'chefdata', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'David Chef Data', 'david.chefdata@entreprise.com', 'ROLE_SECTION', 8, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(10, 'chefinfra', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Emma Chef Infra', 'emma.chefinfra@entreprise.com', 'ROLE_SECTION', 9, 1);

-- Niveau 5 : RESPONSABLES PROJET
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(11, 'resp1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Lucas Projet Web', 'lucas.resp1@entreprise.com', 'ROLE_RESPONSABLE', 7, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(12, 'resp2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Nina Projet Mobile', 'nina.resp2@entreprise.com', 'ROLE_RESPONSABLE', 7, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(13, 'resp3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Omar Projet Data', 'omar.resp3@entreprise.com', 'ROLE_RESPONSABLE', 8, 1);
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(14, 'resp4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Paul Projet Cloud', 'paul.resp4@entreprise.com', 'ROLE_RESPONSABLE', 9, 1);

-- Observateur général (transverse)
INSERT INTO utilisateur (id, username, password, nom_complet, email, role, structure_id, actif) VALUES
(15, 'audit', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Audit Transverse', 'audit@entreprise.com', 'ROLE_OBSERVATEUR_GENERAL', NULL, 1);

-- =============================================================================
-- 3. INTÉRIMS (Délégations temporaires)
-- =============================================================================
-- Intérim : Claire (Chef Web) assure l'intérim de Thomas (Service Dev) du 01/11 au 30/11/2026
INSERT INTO interim_delegation (id, titulaire_id, interimaire_id, structure_id, niveau_delegue, date_debut, date_fin, actif, motif) VALUES
(1, 5, 8, 4, 'SERVICE', DATE '2026-11-01', DATE '2026-11-30', 1, 'Congés annuels du responsable service');

-- Intérim : Pierre (Div SI) assure l'intérim de Marie (Direction) du 15/11 au 15/12/2026
INSERT INTO interim_delegation (id, titulaire_id, interimaire_id, structure_id, niveau_delegue, date_debut, date_fin, actif, motif) VALUES
(2, 2, 3, 1, 'DIRECTION', DATE '2026-11-15', DATE '2026-12-15', 1, 'Mission à l''étranger');

-- =============================================================================
-- 4. PROJETS (Enrichis avec createur_id, structure_id, action_en_cours, date_derniere_action)
-- =============================================================================
INSERT INTO projet (id, code, intitule, description, type_projet, annee_debut, annee_fin_prevue, responsable_id, createur_id, structure_id, action_en_cours, date_derniere_action) VALUES
(1, 'PRJ-PORTAIL-CLIENT', 'Portail E-Commerce & Service Client', 'Plateforme web self-service pour les clients et gestion centralisée des commandes.', 'PLURIANNUEL', 2024, 2027, 11, 8, 7, 1, TIMESTAMP '2026-11-17 14:10:00');

INSERT INTO projet (id, code, intitule, description, type_projet, annee_debut, annee_fin_prevue, responsable_id, createur_id, structure_id, action_en_cours, date_derniere_action) VALUES
(2, 'PRJ-ERP-LOGISTIQUE', 'Modernisation ERP Gestion des Stocks', 'Refonte du système de tracking en temps réel des entrepôts et approvisionnements.', 'PLURIANNUEL', 2025, 2028, 14, 3, 9, 1, TIMESTAMP '2026-11-17 11:25:00');

INSERT INTO projet (id, code, intitule, description, type_projet, annee_debut, annee_fin_prevue, responsable_id, createur_id, structure_id, action_en_cours, date_derniere_action) VALUES
(3, 'PRJ-NOUVEAU-IA', 'Assistant IA Support Utilisateur', 'Chatbot génératif basé sur LLM pour l assistance niveau 1 des collaborateurs.', 'NOUVEAU', 2026, 2026, 13, 9, 8, 0, TIMESTAMP '2026-11-10 09:00:00');

INSERT INTO projet (id, code, intitule, description, type_projet, annee_debut, annee_fin_prevue, responsable_id, createur_id, structure_id, action_en_cours, date_derniere_action) VALUES
(4, 'PRJ-LEGACY-COMPTA', 'Système Comptable Historique v1', 'Logiciel comptable historique maintenu en conditions opérationnelles.', 'ANCIEN', 2019, 2023, 12, 10, 9, 0, TIMESTAMP '2026-10-02 09:00:00');

INSERT INTO projet (id, code, intitule, description, type_projet, annee_debut, annee_fin_prevue, responsable_id, createur_id, structure_id, action_en_cours, date_derniere_action) VALUES
(5, 'PRJ-APP-MOBILE', 'Application Mobile Collaborateurs', 'Application native iOS/Android pour accès aux services RH et IT en mobilité.', 'NOUVEAU', 2026, 2027, 12, 8, 7, 1, TIMESTAMP '2026-11-18 16:30:00');

INSERT INTO projet (id, code, intitule, description, type_projet, annee_debut, annee_fin_prevue, responsable_id, createur_id, structure_id, action_en_cours, date_derniere_action) VALUES
(6, 'PRJ-DATA-LAKE', 'Data Lake & Analytics Centralisé', 'Centralisation des données d''entreprise et mise à disposition pour BI/IA.', 'PLURIANNUEL', 2025, 2028, 13, 9, 8, 0, TIMESTAMP '2026-11-05 10:00:00');

-- =============================================================================
-- 5. BILANS ANNUELS POUR 2026 (Enrichis avec situation_details, perspectives_annee_suivante, inclus_rapport_annuel)
-- =============================================================================
INSERT INTO bilan_annuel (id, projet_id, annee, situation_projet, actions_annee_en_cours, situation_details, fonctionnalites_principales, perspectives_annee_suivante, commentaires, inclus_rapport_annuel) VALUES
(1, 1, 2026, 'EN_BONNE_VOIE', 
 'Déploiement de la passerelle de paiement v2, migration sur Kubernetes, formation de 40 agents du service client, mise en place des tests automatisés.', 
 'Le projet avance conformément au planning initial. Satisfaction utilisateur mesurée à 88%. Les jalons techniques sont respectés. Aucune alerte majeure identifiée. L''équipe est stable et productive.',
 'Authentification SSO OAuth2, Paiement sécurisé multi-devises, Tableau de bord client interactif.', 
 'Généralisation du portail à l''international (support multi-langues), Intégration CRM Salesforce, Déploiement application mobile native.',
 'Le projet avance conformément au planning initial. Satisfaction utilisateur mesurée à 88%.',
 1);

INSERT INTO bilan_annuel (id, projet_id, annee, situation_projet, actions_annee_en_cours, situation_details, fonctionnalites_principales, perspectives_annee_suivante, commentaires, inclus_rapport_annuel) VALUES
(2, 2, 2026, 'SOUS_VIGILANCE', 
 'Audit de performance des bases de données de stock, intégration partielle des lecteurs code-barres industriels, résolution de conflits de synchronisation ERP.', 
 'Retard de 3 semaines constaté suite au délai de livraison des terminaux industriels. Plan de rattrapage en cours avec renforcement de l''équipe. Risque sur la recette utilisateur prévue fin décembre.',
 'Traçabilité RFID en temps réel, Prédiction de rupture de stock par IA, Module inventaire mobile.', 
 'Finalisation déploiement terminaux industriels (T1 2027), Extension module prédiction IA à 50 entrepôts, Intégration fournisseurs en mode collaboratif.',
 'Retard de 3 semaines constaté suite au délai de livraison des terminaux industriels. Plan de rattrapage en cours.',
 1);

INSERT INTO bilan_annuel (id, projet_id, annee, situation_projet, actions_annee_en_cours, situation_details, fonctionnalites_principales, perspectives_annee_suivante, commentaires, inclus_rapport_annuel) VALUES
(3, 3, 2026, 'EN_BONNE_VOIE',
 'Cadrage projet, sélection du modèle open-source, réalisation du prototype POC.',
 'Démarrage rapide et adoption enthousiaste lors des démos. Prototype validé par la direction. En phase de recette technique avant mise en production pilote.',
 'Indexation base documentaire, Moteur RAG, Interface conversationnelle.',
 'Industrialisation et déploiement en production (T1 2027), Extension aux langues étrangères, Intégration base de connaissances juridique.',
 'Démarrage rapide et adoption enthousiaste lors des démos.',
 1);

INSERT INTO bilan_annuel (id, projet_id, annee, situation_projet, actions_annee_en_cours, situation_details, fonctionnalites_principales, perspectives_annee_suivante, commentaires, inclus_rapport_annuel) VALUES
(4, 4, 2026, 'TERMINE',
 'Maintenance corrective courante, application des correctifs de sécurité mensuels.',
 'Projet en mode maintenance pure. Aucune évolution fonctionnelle planifiée. Équipe réduite à 1 personne pour support. Budget maintenu pour corrections critiques uniquement.',
 'Maintenance corrective, Correctifs sécurité, Support utilisateur niveau 1.',
 'Migration vers nouvelle solution comptable (projet distinct PRJ-NEW-COMPTA 2027), Arrêt progressif du système legacy.',
 'Projet en mode maintenance pure. Aucune évolution fonctionnelle planifiée.',
 0);

INSERT INTO bilan_annuel (id, projet_id, annee, situation_projet, actions_annee_en_cours, situation_details, fonctionnalites_principales, perspectives_annee_suivante, commentaires, inclus_rapport_annuel) VALUES
(5, 5, 2026, 'EN_BONNE_VOIE',
 'Conception UX/UI finalisée, architecture technique validée, début développement sprint 1.',
 'Projet lancé selon planning. Architecture micro-services validée par l''architecte entreprise. Équipe complète (8 devs) opérationnelle.',
 'Authentification biométrique, Synchronisation offline, Notifications push intelligentes, Tableau de bord RH mobile.',
 'Lancement bêta interne (T2 2027), Déploiement App Store / Play Store (T3 2027), Fonctionnalités RH avancées (congés, notes de frais).',
 'Nouveau projet 2026, dynamique positive.',
 1);

INSERT INTO bilan_annuel (id, projet_id, annee, situation_projet, actions_annee_en_cours, situation_details, fonctionnalites_principales, perspectives_annee_suivante, commentaires, inclus_rapport_annuel) VALUES
(6, 6, 2026, 'SOUS_VIGILANCE',
 'Ingestion sources de données principales (ERP, CRM, RH), modélisation datamart vente, premières restitutions Power BI.',
 'Complexité d''intégration sous-estimée. Qualité des données sources perfectible. Nécessite ateliers métier supplémentaires pour valider les règles de gestion.',
 'Datamart Ventes unifié, Datamart RH, Datamart Finance, Portail Self-Service BI.',
 'Finalisation datamarts cœur de métier (T1 2027), Ouverture aux datas scientists (MLOps), Gouvernance données (Data Catalog, Data Quality).',
 'Projet structurant mais complexité technique élevée.',
 1);

-- =============================================================================
-- 6. FONCTIONNALITÉS AVEC STATUTS UNITAIRES
-- =============================================================================
-- Projet 1 : Portail E-Commerce
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(1, 1, 1, 'FCT-AUTH-SSO', 'Authentification SSO et gestion de profil', 'Connexion via Google / Microsoft et gestion des données personnelles RGPD.', 'TERMINE', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(2, 1, 1, 'FCT-PAY-MULTI', 'Passerelle de paiement Stripe & PayPal', 'Support multi-devises avec gestion sécurisée 3D-Secure v2.', 'TERMINE', 1, 'CRITIQUE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(3, 1, 1, 'FCT-DASH-CLI', 'Tableau de bord de suivi des commandes', 'Affichage dynamique de l historique et du statut de livraison en temps réel.', 'EN_COURS', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(4, 1, 1, 'FCT-CHAT-SUPP', 'Module de messagerie instantanée support', 'Chat direct avec le service client et système de tickets d assistance.', 'EN_RECETTE', 0, 'MOYENNE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(5, 1, 1, 'FCT-NOTIF-SMS', 'Notifications par SMS et push mobile', 'Alertes sur changement de statut d expédition.', 'A_FAIRE', 0, 'BASSE');

-- Projet 2 : ERP Logistique
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(6, 2, 2, 'FCT-TRACE-RFID', 'Traçabilité des palettes par puces RFID', 'Scan automatisé lors de l entrée et sortie des zones de stockage.', 'EN_COURS', 1, 'CRITIQUE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(7, 2, 2, 'FCT-PRED-RUPT', 'Moteur de prévision des ruptures de stocks', 'Algorithme d alerte préventive selon l historique des ventes.', 'EN_COURS', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(8, 2, 2, 'FCT-APP-MOBIL', 'Application tablette magasinier', 'Interface de saisie et de contrôle des stocks déconnectée.', 'BLOQUE', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(9, 2, 2, 'FCT-EXP-EXCEL', 'Générateur de rapports d inventaire CSV/Excel', 'Exportation des bilans comptables périodiques.', 'TERMINE', 0, 'MOYENNE');

-- Projet 3 : Assistant IA
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(10, 3, 3, 'FCT-IA-RAG', 'Indexation et recherche sémantique RAG', 'Vectorisation des documents et base vectorielle.', 'EN_RECETTE', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(11, 3, 3, 'FCT-IA-CHAT', 'Interface conversationnelle utilisateur', 'Chat web avec historique et suggestions contextuelles.', 'EN_COURS', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(12, 3, 3, 'FCT-IA-ADMIN', 'Console d administration du modèle', 'Supervision des requêtes, feedback utilisateur, réentraînement.', 'A_FAIRE', 0, 'MOYENNE');

-- Projet 4 : Comptable Legacy
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(13, 4, 4, 'FCT-LEG-MAINT', 'Maintenance corrective courante', 'Correction d anomalies signalées par les utilisateurs.', 'EN_COURS', 0, 'MOYENNE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(14, 4, 4, 'FCT-LEG-SEC', 'Correctifs de sécurité mensuels', 'Application des patchs de sécurité fournisseur.', 'TERMINE', 1, 'CRITIQUE');

-- Projet 5 : App Mobile
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(15, 5, 5, 'FCT-MOB-AUTH', 'Authentification biométrique (FaceID/TouchID)', 'Connexion sécurisée sans mot de passe.', 'EN_COURS', 1, 'CRITIQUE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(16, 5, 5, 'FCT-MOB-OFFLINE', 'Mode hors-ligne avec synchronisation', 'Accès aux données en mode avion, sync à la reconnexion.', 'A_FAIRE', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(17, 5, 5, 'FCT-MOB-PUSH', 'Notifications push contextuelles', 'Alertes personnalisées selon profil et localisation.', 'A_FAIRE', 0, 'MOYENNE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(18, 5, 5, 'FCT-MOB-RH', 'Espace RH mobile (congés, notes de frais)', 'Demande congés, validation manager, suivi notes de frais.', 'A_FAIRE', 1, 'HAUTE');

-- Projet 6 : Data Lake
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(19, 6, 6, 'FCT-DL-INGEST', 'Pipeline d ingestion multi-sources', 'Connecteurs ERP, CRM, RH, fichiers plats, API.', 'EN_COURS', 1, 'CRITIQUE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(20, 6, 6, 'FCT-DL-MART-VENTE', 'Datamart Ventes unifié', 'Indicateurs CA, marge, panier moyen, top produits.', 'EN_RECETTE', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(21, 6, 6, 'FCT-DL-MART-RH', 'Datamart RH', 'Effectifs, turnover, formation, paie.', 'A_FAIRE', 1, 'HAUTE');
INSERT INTO fonctionnalite (id, projet_id, bilan_annuel_id, code, intitule, description, statut, est_principale, priorite) VALUES
(22, 6, 6, 'FCT-DL-PORTAL', 'Portail Self-Service BI', 'Accès autonomes aux rapports et dashboards.', 'A_FAIRE', 0, 'MOYENNE');

COMMIT;
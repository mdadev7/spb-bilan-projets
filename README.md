# 🚀 Application Bilan Annuel & Gestion des Fonctionnalités de Projets

Application backend complète développée avec **Java** et **Spring Boot 3.1.3**, connectée à une base de données **MySQL**.

Cette application permet de gérer les projets informatiques, leurs fonctionnalités (avec suivi unitaire des statuts) et d'établir le **bilan annuel / situation par projet** selon les critères attendus :
- **Année** (ex: 2026)
- **Intitulé** du projet
- **Description** globale du projet
- **Liste des fonctionnalités avec statut pour chacune** (`A_FAIRE`, `EN_COURS`, `EN_RECETTE`, `TERMINE`, `BLOQUE`, `ANNULE`)
- **Fonctionnalités principales** (synthèse et identification des fonctionnalités phares)
- **Actions durant l'année en cours** (jalons, réalisations, déploiements menés sur l'exercice)
- **Situation de projet** (météo et état global : `EN_BONNE_VOIE`, `SOUS_VIGILANCE`, `CRITIQUE`, `TERMINE`, `SUSPENDU`)

---

## 🛠️ Stack Technique

- **Langage :** Java 17+ (compatible Java 21)
- **Framework :** Spring Boot 3.1.3
  - `spring-boot-starter-web` (API RESTful)
  - `spring-boot-starter-data-jpa` (Persistance Hibernate 6)
  - `spring-boot-starter-validation` (Contrôles d'intégrité Bean Validation)
- **Base de données :** MySQL 8.0+ / MariaDB
- **Driver JDBC :** `com.mysql:mysql-connector-j`
- **Documentation & Test API :** Springdoc OpenAPI / Swagger UI 2.2.0
- **Utilitaires :** Project Lombok
- **Gestionnaire de build :** Maven

---

## 📂 Structure du Projet

```text
bilan-projets/
├── pom.xml                               # Configuration Maven (Spring Boot 3.1.3, MySQL, etc.)
├── docker-compose-mysql.yml              # Lancement rapide de MySQL en conteneur Docker
├── README.md                             # Documentation complète
└── src/
    ├── main/
    │   ├── java/com/bilanprojets/
    │   │   ├── BilanProjetsApplication.java # Point d'entrée Spring Boot
    │   │   ├── config/
    │   │   │   ├── OpenApiConfig.java       # Configuration Swagger UI
    │   │   │   ├── WebCorsConfig.java       # Configuration CORS pour frontends
    │   │   │   └── DataInitializer.java     # Injection de données de test au démarrage
    │   │   ├── controller/
    │   │   │   ├── ProjetController.java    # Endpoints CRUD projets
    │   │   │   ├── BilanAnnuelController.java # Endpoints bilans annuels & fiches complètes
    │   │   │   ├── FonctionnaliteController.java # Endpoints fonctionnalités & statuts
    │   │   │   └── SyntheseAnnuelleController.java # Tableau de bord annuel global
    │   │   ├── dto/
    │   │   │   ├── ProjetDTO.java
    │   │   │   ├── BilanAnnuelDTO.java
    │   │   │   ├── FonctionnaliteDTO.java
    │   │   │   ├── SituationProjetFicheDTO.java # Fiche annuelle complète (conforme au prompt)
    │   │   │   └── SyntheseAnnuelleDTO.java
    │   │   ├── entity/
    │   │   │   ├── Projet.java
    │   │   │   ├── BilanAnnuel.java
    │   │   │   ├── Fonctionnalite.java
    │   │   │   ├── SituationProjet.java     # Enum Situation / Météo du projet
    │   │   │   └── StatutFonctionnalite.java # Enum Statut des fonctionnalités
    │   │   ├── exception/
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   ├── BusinessException.java
    │   │   │   ├── ErrorResponse.java
    │   │   │   └── GlobalExceptionHandler.java # Gestion centralisée des erreurs HTTP
    │   │   ├── repository/
    │   │   │   ├── ProjetRepository.java
    │   │   │   ├── BilanAnnuelRepository.java
    │   │   │   └── FonctionnaliteRepository.java
    │   │   └── service/
    │   │       ├── ProjetService.java
    │   │       ├── BilanAnnuelService.java
    │   │       ├── FonctionnaliteService.java
    │   │       └── impl/
    │   └── resources/
    │       ├── application.properties       # Configuration de la base MySQL et JPA
    │       └── db/
    │           ├── schema.sql               # Script DDL SQL
    │           └── data.sql                 # Jeu de données d'exemple SQL
    └── test/
        └── java/com/bilanprojets/
            └── BilanProjetsApplicationTests.java
```

---

## ⚙️ Configuration de la Base de Données MySQL

Le fichier `src/main/resources/application.properties` contient la configuration de connexion :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bilan_projets_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Création et mise à jour automatique des tables par Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Astuce :** Vous pouvez modifier `username` et `password` selon vos paramètres locaux de MySQL.

### Option rapide avec Docker (Recommandé) :
Un fichier `docker-compose-mysql.yml` est fourni à la racine du projet :
```bash
docker compose -f docker-compose-mysql.yml up -d
```
Cela démarrera immédiatement un serveur MySQL 8.0 accessible sur le port `3306` avec le mot de passe root configuré (`root_password`).

---

## ▶️ Compilation et Démarrage

### Avec Maven installé :
```bash
cd bilan-projets
mvn clean spring-boot:run
```

### Ou en générant le fichier JAR exécutable :
```bash
mvn clean package -DskipTests
java -jar target/bilan-projets-1.0.0-SNAPSHOT.jar
```

---

## 📖 Interface Swagger UI (Documentation Interactive)

Une fois l'application démarrée, ouvrez votre navigateur à l'adresse suivante :

👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Vous y trouverez l'ensemble des endpoints documentés et directement testables avec des exemples de requêtes.

---

## 🌐 Principaux Endpoints REST

### 1. Fiche Complète de Situation par Projet
Permet de consulter ou d'enregistrer en un seul appel l'ensemble des informations attendues pour un projet et son bilan annuel :

- **`GET /api/projets/{id}/situation/{annee}`** :
  Retourne la situation complète d'un projet pour l'année donnée (ex: `2026`).

- **`POST /api/bilans/fiche-complete`** :
  Crée ou met à jour le projet, sa situation de l'année, ses actions, ses fonctionnalités principales et sa liste de fonctionnalités avec statuts.

#### Exemple de Payload JSON (`POST /api/bilans/fiche-complete`) :
```json
{
  "codeProjet": "PRJ-RH-2026",
  "intitule": "Portail SIRH & Gestion des Congés",
  "description": "Automatisation du processus de demande de congés et validation managériale.",
  "responsable": "Sarah Connor",
  "annee": 2026,
  "situationProjet": "EN_BONNE_VOIE",
  "actionsAnneeEnCours": "Ateliers de cadrage avec la DRH, validation des maquettes Figma, mise en place de la CI/CD GitLab, interfaçage avec le logiciel de paie.",
  "fonctionnalitesPrincipales": "Workflow de validation multi-niveaux, Synchronisation des calendriers Outlook, Export paie mensuel.",
  "commentaires": "Phase pilote lancée auprès de 50 collaborateurs.",
  "listeFonctionnalites": [
    {
      "code": "FCT-SIRH-AUTH",
      "intitule": "Connexion sécurisée Active Directory",
      "description": "Authentification unique des salariés.",
      "statut": "TERMINE",
      "estPrincipale": true,
      "priorite": "HAUTE"
    },
    {
      "code": "FCT-SIRH-WORKFLOW",
      "intitule": "Validation des demandes de congés N+1 / N+2",
      "description": "Envoi d'e-mails et circuit d'approbation.",
      "statut": "EN_COURS",
      "estPrincipale": true,
      "priorite": "CRITIQUE"
    },
    {
      "code": "FCT-SIRH-CALENDAR",
      "intitule": "Affichage planning d'équipe interactif",
      "description": "Visualisation des absences par service.",
      "statut": "A_FAIRE",
      "estPrincipale": false,
      "priorite": "MOYENNE"
    }
  ]
}
```

### 2. Gestion des Projets (`/api/projets`)
- `GET /api/projets` : Lister tous les projets (avec paramètre de recherche `?search=...`).
- `GET /api/projets/{id}` : Récupérer un projet par ID.
- `GET /api/projets/code/{code}` : Récupérer un projet par son code unique.
- `POST /api/projets` : Créer un projet.
- `PUT /api/projets/{id}` : Modifier un projet.
- `DELETE /api/projets/{id}` : Supprimer un projet et ses bilans/fonctionnalités associés.

### 3. Gestion des Bilans Annuels (`/api/bilans`)
- `GET /api/bilans/annee/{annee}` : Liste des bilans pour une année (filtrage possible par `?situation=EN_BONNE_VOIE`).
- `GET /api/bilans/projet/{projetId}` : Historique annuel d'un projet.
- `POST /api/bilans` : Enregistrer un bilan annuel.
- `PUT /api/bilans/{id}` : Modifier un bilan annuel.

### 4. Gestion des Fonctionnalités et Statuts (`/api/fonctionnalites`)
- `POST /api/projets/{id}/fonctionnalites` : Ajouter une fonctionnalité à un projet.
- `PATCH /api/fonctionnalites/{id}/statut?statut=TERMINE` : Mise à jour rapide du statut d'une fonctionnalité (`A_FAIRE`, `EN_COURS`, `EN_RECETTE`, `TERMINE`, `BLOQUE`, `ANNULE`).
- `PUT /api/fonctionnalites/{id}` : Modification complète d'une fonctionnalité.
- `DELETE /api/fonctionnalites/{id}` : Suppression d'une fonctionnalité.

### 5. Synthèse & Tableau de Bord (`/api/synthese`)
- **`GET /api/synthese/annee/{annee}`** :
  Fournit la météo globale de l'ensemble des projets de l'année :
  - Nombre total de projets suivis.
  - Répartition par situation (`EN_BONNE_VOIE`, `SOUS_VIGILANCE`, `CRITIQUE`, `TERMINE`, `SUSPENDU`).
  - Total des fonctionnalités et fonctionnalités terminées.
  - Taux moyen d'avancement (%).
  - Liste de toutes les fiches de synthèse par projet.

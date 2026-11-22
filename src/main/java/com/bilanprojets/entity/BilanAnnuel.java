package com.bilanprojets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant le bilan annuel et la situation d'un projet pour une année donnée.
 * Contient les sections structurées pour le rapport annuel : actions, situation, perspectives.
 */
@Entity
@Table(
    name = "bilan_annuel",
    uniqueConstraints = @UniqueConstraint(name = "uk_projet_annee", columnNames = {"projet_id", "annee"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BilanAnnuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projet_id", nullable = false)
    @JsonIgnoreProperties({"bilans", "fonctionnalites"})
    private Projet projet;

    @NotNull(message = "L'année du bilan est obligatoire")
    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Enumerated(EnumType.STRING)
    @Column(name = "situation_projet", nullable = false, length = 50)
    private SituationProjet situationProjet;

    /**
     * Actions menées, jalons et réalisations durant l'année (exercice X)
     * Alimenté par le responsable projet tout au long de l'année
     */
    @Lob
    @Column(name = "actions_annee_en_cours", columnDefinition = "TEXT")
    private String actionsAnneeEnCours;

    /**
     * Détail de la situation et avancement (météo, risques, état d'avancement global)
     * Section "Situation du projet" du rapport Word
     */
    @Lob
    @Column(name = "situation_details", columnDefinition = "TEXT")
    private String situationDetails;

    /**
     * Synthèse des fonctionnalités majeures et prioritaires
     * Section "Fonctionnalités principales" du rapport Word
     */
    @Lob
    @Column(name = "fonctionnalites_principales", columnDefinition = "TEXT")
    private String fonctionnalitesPrincipales;

    /**
     * Actions prévues pour l'année suivante (X+1)
     * Section "Perspectives" du rapport Word
     */
    @Lob
    @Column(name = "perspectives_annee_suivante", columnDefinition = "TEXT")
    private String perspectivesAnneeSuivante;

    /**
     * Commentaires libres (ancien champ, conservé pour compatibilité)
     * Peut contenir des perspectives ou notes diverses
     */
    @Lob
    @Column(name = "commentaires", columnDefinition = "TEXT")
    private String commentaires;

    /**
     * Indique si ce projet doit être inclus dans le rapport annuel consolidé.
     * Géré par cases à cocher dans l'écran de synthèse annuelle.
     */
    @Column(name = "inclus_rapport_annuel", nullable = false)
    @Builder.Default
    private Boolean inclusRapportAnnuel = true;

    @CreationTimestamp
    @Column(name = "date_evaluation", updatable = false)
    private LocalDateTime dateEvaluation;

    @UpdateTimestamp
    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    @OneToMany(mappedBy = "bilanAnnuel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("bilanAnnuel")
    @Builder.Default
    private List<Fonctionnalite> fonctionnalites = new ArrayList<>();

    public void addFonctionnalite(Fonctionnalite f) {
        fonctionnalites.add(f);
        f.setBilanAnnuel(this);
    }
}
package com.bilanprojets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entité représentant une fonctionnalité d'un projet avec son statut d'avancement.
 */
@Entity
@Table(name = "fonctionnalite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fonctionnalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projet_id", nullable = false)
    @JsonIgnoreProperties({"bilans", "fonctionnalites"})
    private Projet projet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bilan_annuel_id")
    @JsonIgnoreProperties({"projet", "fonctionnalites"})
    private BilanAnnuel bilanAnnuel;

    @Column(name = "code", length = 50)
    private String code;

    @NotBlank(message = "L'intitulé de la fonctionnalité est obligatoire")
    @Column(name = "intitule", nullable = false, length = 255)
    private String intitule;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Le statut de la fonctionnalité est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 50)
    @Builder.Default
    private StatutFonctionnalite statut = StatutFonctionnalite.A_FAIRE;

    @Column(name = "est_principale", nullable = false)
    @Builder.Default
    private Boolean estPrincipale = false;

    @Column(name = "priorite", length = 30)
    @Builder.Default
    private String priorite = "MOYENNE";

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;
}

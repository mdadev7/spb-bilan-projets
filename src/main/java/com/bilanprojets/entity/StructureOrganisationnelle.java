package com.bilanprojets.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant la structure organisationnelle hiérarchique (5 niveaux) :
 * DIRECTION > DIVISION > SERVICE > SECTION > (éventuellement sous-sections)
 * 
 * Principe : Héritage descendant complet des privilèges.
 */
@Entity
@Table(name = "structure_organisationnelle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StructureOrganisationnelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code de la structure est obligatoire")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotBlank(message = "Le libellé de la structure est obligatoire")
    @Column(name = "libelle", nullable = false, length = 150)
    private String libelle;

    /**
     * Niveau hiérarchique : DIRECTION, DIVISION, SERVICE, SECTION
     */
    @NotNull(message = "Le niveau est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "niveau", nullable = false, length = 20)
    private NiveauStructure niveau;

    /**
     * Structure parente (hiérarchie)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"enfants", "utilisateurs", "projets"})
    private StructureOrganisationnelle parent;

    /**
     * Structures enfants (sous-structures)
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("parent")
    @OrderBy("niveau ASC, libelle ASC")
    @Builder.Default
    private List<StructureOrganisationnelle> enfants = new ArrayList<>();

    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    /**
     * Utilisateurs rattachés à cette structure
     */
    @OneToMany(mappedBy = "structure", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("structure")
    @Builder.Default
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    /**
     * Projets hébergés par cette structure
     */
    @OneToMany(mappedBy = "structure", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("structure")
    @Builder.Default
    private List<Projet> projets = new ArrayList<>();

    /**
     * Intérims/délégations concernant cette structure
     */
    @OneToMany(mappedBy = "structure", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("structure")
    @Builder.Default
    private List<InterimDelegation> interimDelegations = new ArrayList<>();

    // Méthodes d'aide pour la navigation hiérarchique

    /**
     * Récupère l'ensemble des IDs de cette structure et de toutes ses sous-structures (descendants)
     */
    public List<Long> getAllDescendantIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(this.id);
        collectDescendantIds(this, ids);
        return ids;
    }

    private void collectDescendantIds(StructureOrganisationnelle structure, List<Long> ids) {
        if (structure.getEnfants() != null) {
            for (StructureOrganisationnelle enfant : structure.getEnfants()) {
                if (Boolean.TRUE.equals(enfant.getActif())) {
                    ids.add(enfant.getId());
                    collectDescendantIds(enfant, ids);
                }
            }
        }
    }

    /**
     * Vérifie si une structure donnée est descendante de cette structure (inclut elle-même)
     */
    public boolean isAncestorOf(StructureOrganisationnelle autre) {
        if (autre == null) return false;
        if (this.id.equals(autre.getId())) return true;
        if (autre.getParent() == null) return false;
        return isAncestorOf(autre.getParent());
    }
}
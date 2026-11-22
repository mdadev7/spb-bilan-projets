package com.bilanprojets.repository;

import com.bilanprojets.entity.InterimDelegation;
import com.bilanprojets.entity.StructureOrganisationnelle;
import com.bilanprojets.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterimDelegationRepository extends JpaRepository<InterimDelegation, Long> {

    List<InterimDelegation> findByTitulaireId(Long titulaireId);

    List<InterimDelegation> findByInterimaireId(Long interimaireId);

    List<InterimDelegation> findByStructureId(Long structureId);

    List<InterimDelegation> findByActifTrue();

    /**
     * Intérims actifs pour un utilisateur donné (en tant qu'intérimaire) à la date actuelle
     */
    @Query("SELECT i FROM InterimDelegation i WHERE i.interimaire.id = :userId AND i.actif = true AND :date BETWEEN i.dateDebut AND i.dateFin")
    List<InterimDelegation> findActifsByInterimaire(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Intérims actifs pour un utilisateur donné (en tant que titulaire) à la date actuelle
     */
    @Query("SELECT i FROM InterimDelegation i WHERE i.titulaire.id = :userId AND i.actif = true AND :date BETWEEN i.dateDebut AND i.dateFin")
    List<InterimDelegation> findActifsByTitulaire(@Param("userId") Long userId, @Param("date") LocalDate date);

    /**
     * Tous les intérims actifs (pour admin/audit) à la date actuelle
     */
    @Query("SELECT i FROM InterimDelegation i WHERE i.actif = true AND :date BETWEEN i.dateDebut AND i.dateFin")
    List<InterimDelegation> findAllActifs(@Param("date") LocalDate date);

    /**
     * Intérims actifs pour une structure donnée à la date actuelle
     */
    @Query("SELECT i FROM InterimDelegation i WHERE i.structure.id = :structureId AND i.actif = true AND :date BETWEEN i.dateDebut AND i.dateFin")
    List<InterimDelegation> findActifsByStructure(@Param("structureId") Long structureId, @Param("date") LocalDate date);
}
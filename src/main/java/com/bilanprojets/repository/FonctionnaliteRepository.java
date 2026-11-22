package com.bilanprojets.repository;

import com.bilanprojets.entity.Fonctionnalite;
import com.bilanprojets.entity.StatutFonctionnalite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FonctionnaliteRepository extends JpaRepository<Fonctionnalite, Long> {

    List<Fonctionnalite> findByProjetId(Long projetId);

    List<Fonctionnalite> findByProjetIdAndBilanAnnuelId(Long projetId, Long bilanAnnuelId);

    List<Fonctionnalite> findByProjetIdAndStatut(Long projetId, StatutFonctionnalite statut);

    List<Fonctionnalite> findByProjetIdAndEstPrincipaleTrue(Long projetId);

    @Query("SELECT COUNT(f) FROM Fonctionnalite f WHERE f.projet.id = :projetId AND f.statut = :statut")
    long countByProjetIdAndStatut(@Param("projetId") Long projetId, @Param("statut") StatutFonctionnalite statut);

    @Query("SELECT COUNT(f) FROM Fonctionnalite f WHERE f.projet.id = :projetId")
    long countByProjetId(@Param("projetId") Long projetId);
}

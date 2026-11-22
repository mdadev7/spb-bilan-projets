package com.bilanprojets.repository;

import com.bilanprojets.entity.BilanAnnuel;
import com.bilanprojets.entity.SituationProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BilanAnnuelRepository extends JpaRepository<BilanAnnuel, Long> {

    List<BilanAnnuel> findByAnnee(Integer annee);

    List<BilanAnnuel> findByProjetId(Long projetId);

    Optional<BilanAnnuel> findByProjetIdAndAnnee(Long projetId, Integer annee);

    List<BilanAnnuel> findByAnneeAndSituationProjet(Integer annee, SituationProjet situationProjet);

    @Query("SELECT b FROM BilanAnnuel b JOIN FETCH b.projet p WHERE b.annee = :annee")
    List<BilanAnnuel> findAllByAnneeWithProjet(@Param("annee") Integer annee);

    @Query("SELECT b FROM BilanAnnuel b JOIN FETCH b.projet p LEFT JOIN FETCH b.fonctionnalites WHERE b.projet.id = :projetId AND b.annee = :annee")
    Optional<BilanAnnuel> findByProjetIdAndAnneeWithDetails(@Param("projetId") Long projetId, @Param("annee") Integer annee);

    @Query("SELECT COUNT(b) FROM BilanAnnuel b WHERE b.annee = :annee AND b.situationProjet = :situation")
    long countByAnneeAndSituation(@Param("annee") Integer annee, @Param("situation") SituationProjet situation);
}

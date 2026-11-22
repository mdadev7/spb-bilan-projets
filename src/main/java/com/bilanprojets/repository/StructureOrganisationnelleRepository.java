package com.bilanprojets.repository;

import com.bilanprojets.entity.NiveauStructure;
import com.bilanprojets.entity.StructureOrganisationnelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StructureOrganisationnelleRepository extends JpaRepository<StructureOrganisationnelle, Long> {

    Optional<StructureOrganisationnelle> findByCode(String code);

    List<StructureOrganisationnelle> findByNiveau(NiveauStructure niveau);

    List<StructureOrganisationnelle> findByParentId(Long parentId);

    List<StructureOrganisationnelle> findByActifTrueOrderByNiveauAscLibelleAsc();

    List<StructureOrganisationnelle> findByActifTrueAndNiveauInOrderByLibelleAsc(List<NiveauStructure> niveaux);

    @Query("SELECT s FROM StructureOrganisationnelle s WHERE s.id = :id OR s.parent.id = :id")
    List<StructureOrganisationnelle> findByIdOrParentId(@Param("id") Long id);

    /**
     * Récupère une structure et tous ses descendants (pour calcul de périmètre hiérarchique)
     */
    @Query("SELECT s FROM StructureOrganisationnelle s WHERE s.id = :id OR s.parent.id = :id OR s.parent.parent.id = :id OR s.parent.parent.parent.id = :id")
    List<StructureOrganisationnelle> findAllDescendants(@Param("id") Long id);

    boolean existsByCode(String code);
}
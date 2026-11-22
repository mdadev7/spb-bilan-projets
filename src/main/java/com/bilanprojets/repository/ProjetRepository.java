package com.bilanprojets.repository;

import com.bilanprojets.entity.Projet;
import com.bilanprojets.entity.TypeProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    Optional<Projet> findByCode(String code);

    boolean existsByCode(String code);

    List<Projet> findByIntituleContainingIgnoreCase(String keyword);

    List<Projet> findByResponsableId(Long responsableId);

    List<Projet> findByResponsableUsername(String username);

    List<Projet> findByTypeProjet(TypeProjet typeProjet);

    @Query("SELECT DISTINCT p FROM Projet p LEFT JOIN FETCH p.responsable LEFT JOIN FETCH p.fonctionnalites WHERE p.id = :id")
    Optional<Projet> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Projet p LEFT JOIN FETCH p.responsable ORDER BY p.dateCreation DESC")
    List<Projet> findAllWithResponsable();
}

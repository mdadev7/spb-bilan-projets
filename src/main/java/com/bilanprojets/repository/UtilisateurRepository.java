package com.bilanprojets.repository;

import com.bilanprojets.entity.RoleUtilisateur;
import com.bilanprojets.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Utilisateur> findByRole(RoleUtilisateur role);

    List<Utilisateur> findByActifTrue();
}

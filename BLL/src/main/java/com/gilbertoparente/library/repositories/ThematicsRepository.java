package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityThematics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThematicsRepository extends JpaRepository<EntityThematics, Integer> {

    // Procurar por nome exato (ex: para não duplicar categorias)
    Optional<EntityThematics> findByDescriptionIgnoreCase(String description);

    // Antes era findByNameContainingIgnoreCase, mudamos para findByDescriptionContainingIgnoreCase
    List<EntityThematics> findByDescriptionContainingIgnoreCase(String description);
}
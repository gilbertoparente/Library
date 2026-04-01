package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityThematics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThematicsRepository extends JpaRepository<EntityThematics, Integer> {


    Optional<EntityThematics> findByDescriptionIgnoreCase(String description);
    List<EntityThematics> findByDescriptionContainingIgnoreCase(String description);
    List<EntityThematics> findAllByOrderByDescriptionAsc();
}
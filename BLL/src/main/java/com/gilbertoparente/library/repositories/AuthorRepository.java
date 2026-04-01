package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityAuthors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<EntityAuthors, Integer> {

    Optional<EntityAuthors> findByUser_IdUser(int idUser);
    boolean existsByUser_IdUser(int idUser);
    List<EntityAuthors> findByStatus(int status);
    List<EntityAuthors> findByAffiliationContainingIgnoreCase(String affiliation);
    List<EntityAuthors> findByUser_NameContainingIgnoreCase(String name);
}
package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityAuthors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<EntityAuthors, Integer> {

    // Procura o autor associado a um utilizador específico
    // O Spring navega de Author -> User -> idUser
    Optional<EntityAuthors> findByUser_IdUser(int userId);

    // Verifica se um utilizador já é autor
    boolean existsByUser_IdUser(int userId);
}
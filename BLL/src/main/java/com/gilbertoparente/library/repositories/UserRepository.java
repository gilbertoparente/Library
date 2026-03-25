package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<EntityUsers, Integer> {

    // O JpaRepository já oferece: save(), findById(), findAll(), deleteById()

    // Busca customizada por email
    Optional<EntityUsers> findByEmail(String email);

    // Verifica se um email já existe (útil para registo)
    boolean existsByEmail(String email);
}
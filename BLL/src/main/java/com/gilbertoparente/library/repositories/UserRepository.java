package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<EntityUsers, Integer> {

    Optional<EntityUsers> findByEmail(String email);
    Optional<EntityUsers> findByIdUser(int idUser);
    boolean existsByEmail(String email);
    List<EntityUsers> findByIsAdmin(Boolean isAdmin);
    List<EntityUsers> findByNameContainingIgnoreCase(String name);
    List<EntityUsers> findAllByOrderByCreatedAtDesc();
}
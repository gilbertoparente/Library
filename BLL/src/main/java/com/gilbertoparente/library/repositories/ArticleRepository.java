package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityArticles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<EntityArticles, Integer> {


    List<EntityArticles> findByTitleContainingIgnoreCase(String title);
    List<EntityArticles> findByThematics_IdThematic(int idThematic);
    List<EntityArticles> findByAuthors_IdUser(int idUser);
    List<EntityArticles> findByStatus(String status);
    List<EntityArticles> findByAuthors_IdUserAndStatus(int idUser, String status);
}
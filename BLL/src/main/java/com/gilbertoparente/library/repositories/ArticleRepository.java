package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityArticles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<EntityArticles, Integer> {

    // Procurar por título (ignora maiúsculas/minúsculas)
    List<EntityArticles> findByTitleContainingIgnoreCase(String title);

    // Procurar artigos de uma determinada temática
    List<EntityArticles> findByThematics_IdThematic(int thematicId);

    // Procurar artigos de um autor específico
    List<EntityArticles> findByAuthors_IdAuthor(int authorId);
}
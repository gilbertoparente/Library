package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityArticles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<EntityArticles, Integer> {
    Optional<EntityArticles> findByTitle(String title);
    List<EntityArticles> findByTitleContainingIgnoreCase(String keyword);
}
package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityArticles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<EntityArticles, Integer> {

    List<EntityArticles> findByTitleContainingIgnoreCase(String title);

    // Filtra por temática (ID)
    List<EntityArticles> findByThematics_IdThematic(int idThematic);

    /**
     * Filtra artigos pelo ID do utilizador associado ao autor.
     */
    List<EntityArticles> findByAuthors_User_IdUser(int idUser);

    /**
     * CORREÇÃO: Removido o 's' de 'externalAuthors' para coincidir com
     * o campo 'externalAuthor' na classe EntityArticles.
     */
    List<EntityArticles> findByExternalAuthorContainingIgnoreCase(String externalAuthor);

    List<EntityArticles> findByStatus(String status);

    /**
     * Filtra artigos por ID do utilizador (autor) e estado do artigo.
     */
    List<EntityArticles> findByAuthors_User_IdUserAndStatus(int idUser, String status);

    /**
     * Pesquisa global: Título, DOI ou Palavras-chave.
     */
    List<EntityArticles> findByTitleContainingIgnoreCaseOrDoiContainingIgnoreCaseOrKeywordsContainingIgnoreCase(
            String title, String doi, String keywords);

    boolean existsByDoi(String doi);
}
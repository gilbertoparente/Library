package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityArticles;
import com.gilbertoparente.library.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    // Listar todos para a TableView do JavaFX
    public List<EntityArticles> findAll() {
        return articleRepository.findAll();
    }

    // Buscar um artigo específico por ID
    public EntityArticles findById(int id) {
        return articleRepository.findById(id).orElse(null);
    }

    // Pesquisar por título
    public List<EntityArticles> searchByTitle(String title) {
        return articleRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional
    public EntityArticles save(EntityArticles article) {
        // Regras de negócio
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new RuntimeException("O título do artigo não pode estar vazio.");
        }

        if (article.getPrice() == null || article.getPrice().doubleValue() < 0) {
            throw new RuntimeException("O preço não pode ser negativo.");
        }

        return articleRepository.save(article);
    }

    @Transactional
    public void delete(int id) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Artigo não encontrado para eliminação.");
        }
    }
}
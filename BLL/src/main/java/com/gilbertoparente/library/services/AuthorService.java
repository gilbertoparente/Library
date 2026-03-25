package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    // Listar todos os autores (ex: para uma lista de créditos)
    public List<EntityAuthors> findAll() {
        return authorRepository.findAll();
    }

    // Buscar por ID do Autor
    public EntityAuthors findById(int id) {
        return authorRepository.findById(id).orElse(null);
    }

    // Buscar por ID do Utilizador
    public EntityAuthors findByUserId(int userId) {
        return authorRepository.findByUser_IdUser(userId).orElse(null);
    }

    @Transactional
    public EntityAuthors save(EntityAuthors author) {
        // Regra: Um utilizador só pode ser autor uma vez
        if (author.getIdAuthor() == 0 && author.getUser() != null) {
            if (authorRepository.existsByUser_IdUser(author.getUser().getIdUser())) {
                throw new RuntimeException("Este utilizador já está registado como autor!");
            }
        }

        if (author.getAffiliation() == null || author.getAffiliation().isEmpty()) {
            author.setAffiliation("Independente"); // Valor padrão
        }

        return authorRepository.save(author);
    }

    @Transactional
    public void delete(int id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
        } else {
            throw new RuntimeException("Autor não encontrado.");
        }
    }
}
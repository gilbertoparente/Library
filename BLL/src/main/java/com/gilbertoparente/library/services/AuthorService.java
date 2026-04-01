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

    public List<EntityAuthors> findAll() {
        return authorRepository.findAll();
    }

    public EntityAuthors findById(int idUser) {
        return authorRepository.findById(idUser).orElse(null);
    }

    @Transactional
    public EntityAuthors save(EntityAuthors author) {

        if (author.getUser() == null) {
            throw new RuntimeException("Não é possível criar um autor sem um utilizador associado!");
        }

        int idUser = author.getUser().getIdUser();

        if (!authorRepository.existsById(idUser)) {

            if (authorRepository.existsByUser_IdUser(idUser)) {
                throw new RuntimeException("Este utilizador já está registado como autor!");
            }
        }

        if (author.getAffiliation() == null || author.getAffiliation().trim().isEmpty()) {
            author.setAffiliation("Independente");
        }

        return authorRepository.save(author);
    }

    @Transactional
    public void delete(int idUser) {

        if (authorRepository.existsById(idUser)) {
            authorRepository.deleteById(idUser);
        } else {
            throw new RuntimeException("Perfil de autor não encontrado.");
        }
    }


    public List<EntityAuthors> findPendingAuthors() {
        return authorRepository.findByStatus(0);
    }

    @Transactional
    public void approveAuthor(int idUser) {
        EntityAuthors author = authorRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado para aprovação."));

        author.setStatus(1); // Aprovado
        authorRepository.save(author);
    }


    @Transactional
    public void suspendAuthor(int idUser) {
        EntityAuthors author = authorRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado."));
        author.setStatus(2); // 2 = Suspenso
        authorRepository.save(author);
    }
}
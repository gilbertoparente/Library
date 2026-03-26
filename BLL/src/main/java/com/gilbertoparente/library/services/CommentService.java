package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityComments;
import com.gilbertoparente.library.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // Listar todos para a moderação global do Administrador
    public List<EntityComments> findAll() {
        return commentRepository.findAll();
    }

    // Listar comentários para exibir na página do artigo
    public List<EntityComments> getCommentsByArticle(int articleId) {
        return commentRepository.findByArticle_IdArticleOrderByCreatedAtDesc(articleId);
    }

    // Buscar um comentário específico
    public EntityComments findById(int id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Transactional
    public EntityComments save(EntityComments comment) {
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new RuntimeException("Não é permitido enviar comentários vazios.");
        }

        if (comment.getIdComment() == 0) {
            comment.setCreatedAt(LocalDateTime.now());
        }

        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(int id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Comentário não encontrado.");
        }
    }

    // Método extra para a moderação (mudar status)
    @Transactional // ESSENCIAL: Sem isto, o Hibernate não faz o "commit" na BD
    public void updateStatus(int idComment, int newStatus) {
        // 1. Vai buscar o comentário original
        EntityComments comment = commentRepository.findById(idComment)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado: " + idComment));

        // 2. Altera o valor no objeto Java
        comment.setStatus(newStatus);

        // 3. Força a gravação
        commentRepository.save(comment);

        // Opcional: imprimir no console para ver se ele passou por aqui
        System.out.println("Status do comentário " + idComment + " atualizado para " + newStatus);
    }
}
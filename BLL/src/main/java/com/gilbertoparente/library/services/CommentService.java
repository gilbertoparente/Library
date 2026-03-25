package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityComments;
import com.gilbertoparente.library.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // Listar comentários para exibir na página do artigo
    public List<EntityComments> getCommentsByArticle(int articleId) {
        return commentRepository.findByArticle_IdArticleOrderByCreatedAtDesc(articleId);
    }

    // Buscar um comentário específico (ex: para editar ou responder)
    public EntityComments findById(int id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Transactional
    public EntityComments save(EntityComments comment) {
        // Validação básica de conteúdo
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new RuntimeException("Não é permitido enviar comentários vazios.");
        }

        // Garante que a data de criação é definida no momento da inserção
        if (comment.getIdComment() == 0) {
            comment.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
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
}
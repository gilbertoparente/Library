package com.gilbertoparente.library.services;

import com.gilbertoparente.library.entities.EntityComments;
import com.gilbertoparente.library.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    public List<EntityComments> findAll() {
        return commentRepository.findAll();
    }


    public List<EntityComments> getApprovedCommentsByArticle(int idArticle) {
        // Status 1 = Aprovado
        return commentRepository.findByArticle_IdArticleAndStatusOrderByCreatedAtDesc(idArticle, 1);
    }

    public EntityComments findById(int id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Transactional
    public EntityComments save(EntityComments comment) {
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new RuntimeException("Não é permitido enviar comentários vazios.");
        }

        if (comment.getIdComment() == 0) {
            comment.setStatus(1);
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

    @Transactional
    public void updateStatus(int idComment, int newStatus) {
        EntityComments comment = commentRepository.findById(idComment)
                .orElseThrow(() -> new RuntimeException("Comentário não encontrado: " + idComment));

        comment.setStatus(newStatus);
        commentRepository.save(comment);

    }


    public long countPendingComments() {
        // 0 = Pendente
        return commentRepository.findByStatus(0).size();
    }
}
package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<EntityComments, Integer> {

    // Procura todos os comentários de um artigo, do mais recente para o mais antigo
    List<EntityComments> findByArticle_IdArticleOrderByCreatedAtDesc(int articleId);

    // Procura todos os comentários feitos por um utilizador específico
    List<EntityComments> findByUser_IdUser(int userId);

    // Procura apenas as respostas de um comentário pai
    List<EntityComments> findByParentComment_IdComment(int parentId);
}
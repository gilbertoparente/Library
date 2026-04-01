package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<EntityComments, Integer> {

    List<EntityComments> findByArticle_IdArticleOrderByCreatedAtDesc(int idArticle);
    List<EntityComments> findByArticle_IdArticleAndStatusOrderByCreatedAtDesc(int idArticle, int status);
    List<EntityComments> findByUser_IdUser(int idUser);
    List<EntityComments> findByParentComment_IdComment(int idParentComment);
    List<EntityComments> findByStatus(int status);
}
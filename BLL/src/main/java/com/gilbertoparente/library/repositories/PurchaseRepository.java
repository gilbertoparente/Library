package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityPurchases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<EntityPurchases, Integer> {

    // O Spring entende que deve procurar o ID dentro da entidade 'user'
    List<EntityPurchases> findByUser_IdUser(int userId);

    // O Spring entende que deve procurar o ID dentro da entidade 'article'
    List<EntityPurchases> findByArticle_IdArticle(int articleId);

    // Listar apenas o que já foi pago
    List<EntityPurchases> findByPaidTrue();
}
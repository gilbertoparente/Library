package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityPurchases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<EntityPurchases, Integer> {


    List<EntityPurchases> findByUser_IdUser(int idUser);
    List<EntityPurchases> findByArticle_IdArticle(int idArticle);
    List<EntityPurchases> findByStatus(String status);
    List<EntityPurchases> findByUser_IdUserAndStatus(int idUser, String status);
    List<EntityPurchases> findAllByOrderByPurchaseDateDesc();
}
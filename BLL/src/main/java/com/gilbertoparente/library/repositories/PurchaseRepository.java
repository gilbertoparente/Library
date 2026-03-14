package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityPurchases;
import com.gilbertoparente.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class PurchaseRepository extends GenericRepository<EntityPurchases> {

    public PurchaseRepository() {
        super(EntityPurchases.class);
    }

    // Buscar compra pelo ID
    public EntityPurchases findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(EntityPurchases.class, id);
        }
    }

    // Buscar todas as compras de um usuário
    public List<EntityPurchases> findByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from EntityPurchases where idUser = :userId", EntityPurchases.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }

    // Buscar todas as compras de um artigo
    public List<EntityPurchases> findByArticleId(int articleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from EntityPurchases where idArticle = :articleId", EntityPurchases.class)
                    .setParameter("articleId", articleId)
                    .list();
        }
    }

    // Atualizar status de pagamento
    public void updatePaidStatus(int id, boolean paid) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityPurchases set paid = :paid where idPurchase = :id")
                    .setParameter("paid", paid)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Atualizar valor da compra
    public void updateAmount(int id, BigDecimal amount) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityPurchases set amount = :amount where idPurchase = :id")
                    .setParameter("amount", amount)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Deletar compra
    public void deleteById(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("delete from EntityPurchases where idPurchase = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Listar todas as compras
    public List<EntityPurchases> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from EntityPurchases", EntityPurchases.class).list();
        }
    }
}
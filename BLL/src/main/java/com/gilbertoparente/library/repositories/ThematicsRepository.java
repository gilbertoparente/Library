package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityThematics;
import com.gilbertoparente.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class ThematicsRepository extends GenericRepository<EntityThematics> {

    public ThematicsRepository() {
        super(EntityThematics.class);
    }

    // Buscar temática por descrição exata
    public EntityThematics findByDescription(String description) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from EntityThematics where description = :desc", EntityThematics.class)
                    .setParameter("desc", description)
                    .uniqueResult();
        }
    }

    // Buscar temáticas que contenham a string (like)
    public List<EntityThematics> searchByDescription(String description) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from EntityThematics where description like :desc", EntityThematics.class)
                    .setParameter("desc", "%" + description + "%")
                    .list();
        }
    }

    // Atualizar descrição
    public void updateDescriptionById(int id, String newDescription) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityThematics set description = :desc where idThematic = :id")
                    .setParameter("desc", newDescription)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Deletar temática (também remove associações com artigos)
    public void deleteById(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Remove associações com artigos
            session.createQuery("delete from com.gilbertoparente.library.entities.EntityArticleThematic where thematicsByIdThematic.idThematic = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            // Remove a temática
            session.createQuery("delete from EntityThematics where idThematic = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            tx.commit();
        }
    }

    // Listar todas as temáticas
    public List<EntityThematics> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from EntityThematics", EntityThematics.class).list();
        }
    }
}
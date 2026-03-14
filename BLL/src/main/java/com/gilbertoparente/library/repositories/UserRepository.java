package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserRepository extends GenericRepository<EntityUsers> {

    public UserRepository() {
        super(EntityUsers.class);
    }

    // Buscar usuário por ID
    public EntityUsers findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(EntityUsers.class, id);
        }
    }

    // Buscar usuário por email
    public EntityUsers findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from EntityUsers where email = :email", EntityUsers.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    // Listar todos os usuários
    public List<EntityUsers> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from EntityUsers", EntityUsers.class).list();
        }
    }

    // Atualizar nome
    public void updateName(int id, String name) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityUsers set name = :name where idUser = :id")
                    .setParameter("name", name)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Atualizar email
    public void updateEmail(int id, String email) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityUsers set email = :email where idUser = :id")
                    .setParameter("email", email)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Atualizar senha
    public void updatePassword(int id, String password) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityUsers set password = :password where idUser = :id")
                    .setParameter("password", password)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Atualizar admin
    public void updateAdminStatus(int id, boolean isAdmin) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityUsers set isAdmin = :isAdmin where idUser = :id")
                    .setParameter("isAdmin", isAdmin)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }

    // Deletar usuário (remove cascata de autores e compras)
    public void deleteById(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery("delete from EntityUsers where idUser = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();
        }
    }
}
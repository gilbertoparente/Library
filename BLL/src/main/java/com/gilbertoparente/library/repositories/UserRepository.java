package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityUsers;
import com.gilbertoparente.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserRepository extends GenericRepository<EntityUsers> implements IUserRepository {

    public UserRepository() {
        super(EntityUsers.class);
    }

    @Override
    public EntityUsers findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(EntityUsers.class, id);
        }
    }

    @Override
    public EntityUsers findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from EntityUsers where email = :email", EntityUsers.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public List<EntityUsers> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from EntityUsers", EntityUsers.class).list();
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
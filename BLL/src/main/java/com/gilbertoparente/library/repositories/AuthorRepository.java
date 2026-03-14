package com.gilbertoparente.library.repositories;

import com.gilbertoparente.library.entities.EntityAuthors;
import com.gilbertoparente.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class AuthorRepository extends GenericRepository<EntityAuthors> {

    public AuthorRepository() {
        super(EntityAuthors.class);
    }

    // Buscar autor pelo id do usuário
    public EntityAuthors findByUserId(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from EntityAuthors a where a.idUser = :userId",
                            EntityAuthors.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
        }
    }

    // Deletar todos os registros de article_author de um autor
    public void deleteAllArticleLinks(int authorId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.createQuery(
                            "delete from EntityArticleAuthor a where a.id.idAuthor = :id")
                    .setParameter("id", authorId)
                    .executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Listar todos os autores
    public List<EntityAuthors> findAllAuthors() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from EntityAuthors", EntityAuthors.class).list();
        }
    }

    // Exemplo de update de afiliação
    public void updateAffiliation(int authorId, String newAffiliation) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createQuery(
                            "update EntityAuthors a set a.affiliation = :aff where a.idAuthor = :id")
                    .setParameter("aff", newAffiliation)
                    .setParameter("id", authorId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
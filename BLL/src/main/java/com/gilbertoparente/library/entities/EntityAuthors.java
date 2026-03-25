package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "authors")
public class EntityAuthors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_author")
    private int idAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private EntityUsers user;

    @Column(name = "affiliation", length = 150)
    private String affiliation;

    @ManyToMany(mappedBy = "authors")
    private Collection<EntityArticles> articles;

    // --- GETTERS E SETTERS ---

    public int getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(int idAuthor) {
        this.idAuthor = idAuthor;
    }

    // ESTE MÉTODO É O QUE FALTAVA PARA O SERVICE FUNCIONAR:
    public EntityUsers getUser() {
        return user;
    }

    public void setUser(EntityUsers user) {
        this.user = user;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public Collection<EntityArticles> getArticles() {
        return articles;
    }

    public void setArticles(Collection<EntityArticles> articles) {
        this.articles = articles;
    }

    // É boa prática ter o equals e hashCode para entidades JPA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityAuthors that = (EntityAuthors) o;
        return idAuthor == that.idAuthor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAuthor);
    }
}
package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "authors")
public class EntityAuthors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_author") // Mapeia para a PK real da tabela
    private int idAuthor;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user") // FK para a tabela de users
    private EntityUsers user;

    @Column(name = "affiliation", length = 150)
    private String affiliation;

    @Column(name = "status")  // 0: Inativo, 1: Ativo, 2: Suspenso
    private int status = 0;

    @ManyToMany(mappedBy = "authors")
    private Collection<EntityArticles> articles;


    public int getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(int idAuthor) {
        this.idAuthor = idAuthor;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Collection<EntityArticles> getArticles() {
        return articles;
    }

    public void setArticles(Collection<EntityArticles> articles) {
        this.articles = articles;
    }


    @Transient
    public String getName() {
        return (user != null) ? user.getName() : null;
    }

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
package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "authors")
public class EntityAuthors {

    @Id
    @Column(name = "id_user")
    private int idUser;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id_user")
    private EntityUsers user;

    @Column(name = "affiliation", length = 150)
    private String affiliation;

    @Column(name = "status")  // 0: Inativo, 1: Ativo, 2: Suspenso
    private int status = 0;

    @ManyToMany(mappedBy = "authors")
    private Collection<EntityArticles> articles;

    // --- GETTERS E SETTERS ---

    public int getIdUser() {
        return idUser;
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



    @Transient // Indica que não existe esta coluna na tabela authors
    public String getName() {
        return (user != null) ? user.getName() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityAuthors that = (EntityAuthors) o;
        return idUser == that.idUser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser);
    }
}
package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "thematics", schema = "public")
public class EntityThematics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thematic")
    private int idThematic;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @ManyToMany(mappedBy = "thematics")
    private Collection<EntityArticles> articles;

    public EntityThematics() {}


    public int getIdThematic() {
        return idThematic;
    }

    public void setIdThematic(int idThematic) {
        this.idThematic = idThematic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<EntityArticles> getArticles() {
        return articles;
    }

    public void setArticles(Collection<EntityArticles> articles) {
        this.articles = articles;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityThematics that = (EntityThematics) o;
        return idThematic == that.idThematic;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idThematic);
    }

    @Override
    public String toString() {
        return description;
    }
}
package com.gilbertoparente.library.entities;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "thematics", schema = "public", catalog = "scientific_library")
public class EntityThematics {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_thematic")
    private int idThematic;
    @Basic
    @Column(name = "description")
    private String description;


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



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityThematics that = (EntityThematics) o;
        return idThematic == that.idThematic && Objects.equals(description, that.description);
    }



}

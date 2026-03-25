package com.gilbertoparente.library.entities;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "public", catalog = "scientific_library")
public class EntityUsers {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_user")
    private int idUser;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "email")
    private String email;
    @Basic
    @Column(name = "password")
    private String password;
    @Basic
    @Column(name = "is_admin")
    private Boolean isAdmin;
    @OneToMany(mappedBy = "user")
    private Collection<EntityAuthors> authorsByIdUser;
    @OneToMany(mappedBy = "user")
    private Collection<EntityPurchases> purchasesByIdUser;

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityUsers that = (EntityUsers) o;
        return idUser == that.idUser && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(isAdmin, that.isAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, name, email, password, isAdmin);
    }

    public Collection<EntityAuthors> getAuthorsByIdUser() {
        return authorsByIdUser;
    }

    public void setAuthorsByIdUser(Collection<EntityAuthors> authorsByIdUser) {
        this.authorsByIdUser = authorsByIdUser;
    }

    public Collection<EntityPurchases> getPurchasesByIdUser() {
        return purchasesByIdUser;
    }

    public void setPurchasesByIdUser(Collection<EntityPurchases> purchasesByIdUser) {
        this.purchasesByIdUser = purchasesByIdUser;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}

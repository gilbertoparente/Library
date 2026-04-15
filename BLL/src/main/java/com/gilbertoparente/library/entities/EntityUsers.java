package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "public")
public class EntityUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int idUser;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EntityAuthors author;

    @OneToMany(mappedBy = "user")
    private Collection<EntityPurchases> purchases;

    @OneToMany(mappedBy = "user")
    private Collection<EntityComments> comments;


    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public EntityAuthors getAuthor() { return author; }
    public void setAuthor(EntityAuthors author) { this.author = author; }

    public Collection<EntityPurchases> getPurchases() { return purchases; }
    public void setPurchases(Collection<EntityPurchases> purchases) { this.purchases = purchases; }

    public Collection<EntityComments> getComments() { return comments; }
    public void setComments(Collection<EntityComments> comments) { this.comments = comments; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityUsers that = (EntityUsers) o;
        return idUser == that.idUser && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, email);
    }


    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "purchases")
public class EntityPurchases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_purchase")
    private int idPurchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", nullable = true)
    private EntityUsers user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_article", nullable = true)
    private EntityArticles article;

    @CreationTimestamp
    @Column(name = "purchase_date", updatable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", length = 20) // estados: pending, paid, failed, refund, canceled
    private String status = "pending";

    public EntityPurchases() {}

    // --- GETTERS E SETTERS ---

    public int getIdPurchase() { return idPurchase; }
    public void setIdPurchase(int idPurchase) { this.idPurchase = idPurchase; }

    public EntityUsers getUser() { return user; }
    public void setUser(EntityUsers user) { this.user = user; }

    public EntityArticles getArticle() { return article; }
    public void setArticle(EntityArticles article) { this.article = article; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Transient
    public String getUserName() {
        return (user != null) ? user.getName() : "Utilizador Removido";
    }

    @Transient
    public String getArticleTitle() {
        return (article != null) ? article.getTitle() : "Artigo Removido";
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityPurchases that = (EntityPurchases) o;
        return idPurchase == that.idPurchase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPurchase);
    }
}
package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "articles")
public class EntityArticles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_article")
    private int idArticle;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "resume", columnDefinition = "TEXT")
    private String resume;

    @Column(name = "publication_date")
    private Date publicationDate;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "vat_rate")
    private Integer vatRate = 6;

    // NOVO: Campo de estado (draft, published, archived)
    @Column(name = "status", length = 20)
    private String status = "draft";

    // NOVO: Timestamp de criação automática
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // NOVO: Timestamp de atualização automática
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "article_author",
            joinColumns = @JoinColumn(name = "id_article"),
            inverseJoinColumns = @JoinColumn(name = "id_author")
    )
    private Collection<EntityAuthors> authors;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
            name = "article_thematic",
            joinColumns = @JoinColumn(name = "id_article"),
            inverseJoinColumns = @JoinColumn(name = "id_thematic")
    )
    private Collection<EntityThematics> thematics;

    @OneToMany(mappedBy = "article")
    private Collection<EntityPurchases> purchases;

    // GETTERS E SETTERS

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getIdArticle() { return idArticle; }
    public void setIdArticle(int idArticle) { this.idArticle = idArticle; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
    public Date getPublicationDate() { return publicationDate; }
    public void setPublicationDate(Date publicationDate) { this.publicationDate = publicationDate; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Integer getVatRate() { return vatRate; }
    public void setVatRate(Integer vatRate) { this.vatRate = vatRate; }
    public Collection<EntityThematics> getThematics() { return thematics; }
    public void setThematics(Collection<EntityThematics> thematics) { this.thematics = thematics; }

    public BigDecimal getFullPrice() {
        if (price == null) return BigDecimal.ZERO;
        if (vatRate == null || vatRate == 0) return price;

        BigDecimal vatMultiplier = new BigDecimal(vatRate)
                .divide(new BigDecimal(100))
                .add(BigDecimal.ONE);

        return price.multiply(vatMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashSet; // Importar HashSet
import java.util.Objects;
import java.util.Set;     // Mudar de Collection para Set

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

    @Column(name = "status", length = 20)
    private String status = "Rescunho";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "doi", unique = true, length = 100)
    private String doi;

    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "external_author")
    private String externalAuthor;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "article_author",
            joinColumns = @JoinColumn(name = "id_article"),
            inverseJoinColumns = @JoinColumn(name = "id_author")
    )
    private Set<EntityAuthors> authors = new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "article_thematic",
            joinColumns = @JoinColumn(name = "id_article"),
            inverseJoinColumns = @JoinColumn(name = "id_thematic")
    )
    private Set<EntityThematics> thematics = new HashSet<>();

    @OneToMany(mappedBy = "article")
    private Set<EntityPurchases> purchases = new HashSet<>();

    // --- GETTERS E SETTERS

    public Set<EntityAuthors> getAuthors() { return authors; }
    public void setAuthors(Set<EntityAuthors> authors) { this.authors = authors; }

    public Set<EntityThematics> getThematics() { return thematics; }
    public void setThematics(Set<EntityThematics> thematics) { this.thematics = thematics; }

    // Manter os outros getters/setters (id, title, status, etc...)
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    public String getExternalAuthor() { return externalAuthor; }
    public void setExternalAuthor(String externalAuthor) { this.externalAuthor = externalAuthor; }

    public BigDecimal getFullPrice() {
        if (price == null) return BigDecimal.ZERO;
        if (vatRate == null || vatRate == 0) return price;

        BigDecimal vatMultiplier = new BigDecimal(vatRate)
                .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP)
                .add(BigDecimal.ONE);

        return price.multiply(vatMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityArticles that = (EntityArticles) o;
        return idArticle == that.idArticle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idArticle);
    }
}
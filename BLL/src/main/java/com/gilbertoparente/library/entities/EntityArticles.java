package com.gilbertoparente.library.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
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

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getVatRate() {
        return vatRate;
    }

    public void setVatRate(Integer vatRate) {
        this.vatRate = vatRate;
    }

    // No final da classe EntityArticles.java

    public Collection<EntityThematics> getThematics() {
        return thematics;
    }

    public void setThematics(Collection<EntityThematics> thematics) {
        this.thematics = thematics;
    }

    /**
     * Método utilitário para calcular o preço final com IVA.
     *
     */
    public BigDecimal getFullPrice() {
        if (price == null) return BigDecimal.ZERO;
        if (vatRate == null || vatRate == 0) return price;

        BigDecimal vatMultiplier = new BigDecimal(vatRate)
                .divide(new BigDecimal(100))
                .add(BigDecimal.ONE);

        return price.multiply(vatMultiplier).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityArticles that = (EntityArticles) o;
        return idArticle == that.idArticle && Objects.equals(title, that.title) && Objects.equals(resume, that.resume) && Objects.equals(publicationDate, that.publicationDate) && Objects.equals(price, that.price) && Objects.equals(filePath, that.filePath) && Objects.equals(vatRate, that.vatRate) && Objects.equals(authors, that.authors) && Objects.equals(thematics, that.thematics) && Objects.equals(purchases, that.purchases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idArticle, title, resume, publicationDate, price, filePath, vatRate, authors, thematics, purchases);
    }
}

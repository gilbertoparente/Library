package com.gilbertoparente.library.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "comments", schema = "public")
public class EntityComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comment")
    private int idComment;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "status") //0: Pendente, 1: Aprovado, 2: Ocultado/Spam
    private int status = 1;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_article", nullable = false)
    private EntityArticles article;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", nullable = false)
    private EntityUsers user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private EntityComments parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private Collection<EntityComments> replies;


    public int getIdComment() { return idComment; }
    public void setIdComment(int idComment) { this.idComment = idComment; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public EntityArticles getArticle() { return article; }
    public void setArticle(EntityArticles article) { this.article = article; }

    public EntityUsers getUser() { return user; }
    public void setUser(EntityUsers user) { this.user = user; }

    public EntityComments getParentComment() { return parentComment; }
    public void setParentComment(EntityComments parentComment) { this.parentComment = parentComment; }

    public Collection<EntityComments> getReplies() { return replies; }
    public void setReplies(Collection<EntityComments> replies) { this.replies = replies; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityComments that = (EntityComments) o;
        return idComment == that.idComment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idComment);
    }
}
package models;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String author;
    private Post post; // Référence au post parent

    // Constructeurs
    public Comment() {
        this.createdAt = LocalDateTime.now();
    }

    public Comment(String content, String author, Post post) {
        this();
        this.content = content;
        this.author = author;
        this.post = post;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    // Méthode utilitaire pour mettre à jour la date de modification
    public void updateTimestamps() {
        this.updatedAt = LocalDateTime.now();
    }
}
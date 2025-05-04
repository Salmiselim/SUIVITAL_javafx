package models;

import java.time.LocalDate;

public class Reclamation {
    private int id;
    private String objet;        // Corresponds to 'sujet' in your model
    private String commentaire;  // Corresponds to 'description' in your model
    private LocalDate date;      // Corresponds to 'date_reclamation' in your model
    private int userId;          // Hardcoded userId

    // Constructor with hardcoded userId as 1
    public Reclamation(int id, String objet, String commentaire, LocalDate date) {
        this.id = id;
        this.objet = objet;
        this.commentaire = commentaire;
        this.date = date;
        this.userId = 1; // Hardcoding the userId as 1
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
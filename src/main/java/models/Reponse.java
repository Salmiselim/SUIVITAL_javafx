package models;

import java.time.LocalDate;

public class Reponse {
    private int id;
    private String objet;        // 'objet' for the response
    private String commentaire;  // 'commentaire' for the response
    private LocalDate date;      // 'date' for the response
    private int userId;          // Hardcoded userId as 1
    private int reclamationId;

    // Constructor with hardcoded userId as 1 and reclamationId as NULL (0)
    public Reponse(int id, String objet, String commentaire, LocalDate date, int reclamationId) {
        this.id = id;
        this.objet = objet;
        this.commentaire = commentaire;
        this.date = date;
        this.userId = 1; // Hardcoding userId as 1
        this.reclamationId = reclamationId;
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

    public int getReclamationId() {
        return reclamationId;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }
}

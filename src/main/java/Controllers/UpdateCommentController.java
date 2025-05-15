package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Comment;
import services.CommentService;
import javafx.stage.Stage;

public class UpdateCommentController {
    @FXML private TextField authorField;
    @FXML private TextArea contentArea;

    private Comment currentComment;
    private final CommentService commentService = new CommentService();

    public void setComment(Comment comment) {
        this.currentComment = comment;
        authorField.setText(comment.getAuthor());
        contentArea.setText(comment.getContent());
    }

    @FXML
    private void handleUpdate() {
        if (currentComment == null) {
            showAlert("Erreur", "Aucun commentaire sélectionné pour modification");
            return;
        }

        try {
            // Validation des champs
            if (authorField.getText().isEmpty() || contentArea.getText().isEmpty()) {
                showAlert("Erreur", "L'auteur et le contenu sont obligatoires");
                return;
            }

            if (authorField.getText().length() < 2) {
                showAlert("Erreur", "Le nom d'auteur doit contenir au moins 2 caractères");
                return;
            }

            if (contentArea.getText().length() < 5) {
                showAlert("Erreur", "Le commentaire doit contenir au moins 5 caractères");
                return;
            }

            // Mise à jour du commentaire
            currentComment.setAuthor(authorField.getText());
            currentComment.setContent(contentArea.getText());
            currentComment.setUpdatedAt(); // Met à jour le timestamp

            // Sauvegarde
            commentService.updateComment(currentComment);

            // Fermeture de la fenêtre
            closeWindow();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la mise à jour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) authorField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
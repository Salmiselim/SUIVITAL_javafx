package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Post;
import services.PostService;
import javafx.stage.Stage;

public class UpdatePostController {
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private TextField authorField;

    private Post currentPost;
    private final PostService postService = new PostService();

    public void setPost(Post post) {
        this.currentPost = post;
        titleField.setText(post.getTitle());
        contentArea.setText(post.getContent());
        authorField.setText(post.getAuthor());
    }

    @FXML
    private void handleUpdate() {
        if (currentPost == null) {
            showAlert("Erreur", "Aucun post sélectionné pour modification");
            return;
        }

        try {
            // Validation des champs
            if (titleField.getText().isEmpty() || contentArea.getText().isEmpty()) {
                showAlert("Erreur", "Le titre et le contenu sont obligatoires");
                return;
            }

            // Mise à jour du post
            currentPost.setTitle(titleField.getText());
            currentPost.setContent(contentArea.getText());
            currentPost.setAuthor(authorField.getText());
            currentPost.setUpdatedAt();

            // Sauvegarde
            postService.updatePost(currentPost);

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
        Stage stage = (Stage) titleField.getScene().getWindow();
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
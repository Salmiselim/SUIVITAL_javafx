package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Post;
import services.PostService;

import java.sql.SQLException;

public class AddPostController {

    // Éléments FXML
    @FXML private TextField authorField;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private Button submitButton;
    @FXML private Button clearButton;

    private final PostService postService = new PostService();

    @FXML
    public void initialize() {
        // Configuration initiale si nécessaire
    }

    @FXML
    private void handleSubmit() {
        try {
            // Récupération des valeurs
            String author = authorField.getText().trim();
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();

            // Validation des champs
            if (author.isEmpty()) {
                showAlert("Erreur", "Le champ Auteur est obligatoire");
                authorField.requestFocus();
                return;
            }

            if (title.isEmpty() || title.length() < 5) {
                showAlert("Erreur", "Le titre doit contenir au moins 5 caractères");
                titleField.requestFocus();
                return;
            }

            if (content.isEmpty() || content.length() < 10) {
                showAlert("Erreur", "Le contenu doit contenir au moins 10 caractères");
                contentArea.requestFocus();
                return;
            }

            // Création du post
            Post newPost = new Post(title, content, author);
            postService.createPost(newPost);

            // Confirmation et fermeture
            showAlert("Succès", "Post publié avec succès!");
            clearForm();

        } catch (SQLException e) {
            showAlert("Erreur Critique", "Échec de la création du post: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private void clearForm() {
        authorField.clear();
        titleField.clear();
        contentArea.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
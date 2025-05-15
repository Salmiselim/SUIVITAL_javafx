package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Comment;
import models.Post;
import services.CommentService;

import java.sql.SQLException;

public class AddCommentController {

    @FXML private TextField authorField;
    @FXML private TextArea commentContent;

    private Post parentPost;
    private final CommentService commentService = new CommentService();

    // Set the parent post for the comment
    public void setParentPost(Post post) {
        this.parentPost = post;
    }

    // Handle form submission
    public boolean handleSubmit() {
        try {
            String author = authorField.getText();
            String content = commentContent.getText();

            if (author.isEmpty() || content.isEmpty()) {
                showAlert("Champs vides", "Veuillez remplir tous les champs.");
                return false;
            }

            if (parentPost == null) {
                showAlert("Erreur", "Aucun post parent n’a été défini.");
                return false;
            }

            Comment newComment = new Comment();
            newComment.setAuthor(author);
            newComment.setContent(content);
            newComment.setPost(parentPost); // ← OBLIGATOIRE !

            commentService.createComment(newComment);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec de l'ajout du commentaire.");
            return false;
        }
    }


    // Configure dialog buttons
    public void configureDialog(Dialog<ButtonType> dialog) {
        System.out.println("[DEBUG] Configuring dialog buttons...");
        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                System.out.println("[DEBUG] OK button clicked");
                return handleSubmit() ? buttonType : null;
            }
            return null;
        });
    }

    // Show error alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Close the window
    private void closeWindow() {
        authorField.getScene().getWindow().hide();
    }
}
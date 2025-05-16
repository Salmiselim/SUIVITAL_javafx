package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import models.Comment;
import models.Post;
import services.CommentService;

import java.sql.SQLException;

public class AddCommentController {

    @FXML private TextField authorField;
    @FXML private TextArea commentContent;
    @FXML private Button submitButton;
    @FXML private Button clearButton;

    private Post parentPost;
    private final CommentService commentService = new CommentService();

    // Set the parent post for the comment
    public void setParentPost(Post post) {
        this.parentPost = post;
    }

    // Handle form submission (lié au bouton Publish)
    @FXML
    private void handleSubmit(ActionEvent event) {
        try {
            String author = authorField.getText();
            String content = commentContent.getText();

            if (author.isEmpty() || content.isEmpty()) {
                showAlert("Empty fields", "Please fill all fields");
                return;
            }

            if (parentPost == null || parentPost.getId() <= 0) {
                showAlert("Error", "No valid parent post defined");
                return;
            }

            Comment newComment = new Comment();
            newComment.setAuthor(author);
            newComment.setContent(content);
            newComment.setPost(parentPost);

            commentService.createComment(newComment);

            // Fermer la fenêtre après succès
            closeWindow(event);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add comment: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred");
        }
    }

    // Handle clear form (lié au bouton Clear)
    @FXML
    private void handleClear(ActionEvent event) {
        authorField.clear();
        commentContent.clear();
    }

    // Show error alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Close the window (adaptée pour utiliser l'event)
    private void closeWindow(ActionEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    // Méthode optionnelle pour fermer sans event
    public void closeWindow() {
        authorField.getScene().getWindow().hide();
    }

    public boolean validateAndSubmit() {
        try {
            String author = authorField.getText();
            String content = commentContent.getText();

            if (author.isEmpty() || content.isEmpty()) {
                showAlert("Empty Fields", "Please fill all fields");
                return false;
            }

            if (parentPost == null) {
                showAlert("Error", "No parent post defined");
                return false;
            }

            Comment newComment = new Comment();
            newComment.setAuthor(author);
            newComment.setContent(content);
            newComment.setPost(parentPost);

            commentService.createComment(newComment);
            return true;

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add comment: " + e.getMessage());
            return false;
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred");
            return false;
        }
    }
}
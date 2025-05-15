package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import models.Comment;
import models.Post;
import services.CommentService;
import services.PostService;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import outils.PdfExporter;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ForumListController {

    @FXML private VBox postsContainer;
    @FXML private TextField searchField;
    @FXML private Button addNewPostButton;

    private final PostService postService = new PostService();
    private final CommentService commentService = new CommentService();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    private List<Post> allPosts;

    @FXML
    public void initialize() {
        addNewPostButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        loadPosts();
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();
        try {
            allPosts = postService.getAllPosts();
            displayPosts(allPosts);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.INFORMATION, "Error", "Failed to load posts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayPosts(List<Post> posts) {
        for (Post post : posts) {
            VBox postContainer = createPostContainer(post);
            postsContainer.getChildren().add(postContainer);
        }
    }

    private VBox createPostContainer(Post post) {
        VBox postContainer = new VBox(10);
        postContainer.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        // Header avec titre et date
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(post.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #2e7d32;");
        Label dateLabel = new Label(post.getCreatedAt().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #757575;");
        titleBox.getChildren().addAll(titleLabel, dateLabel);

        // Auteur
        HBox authorBox = new HBox(10);
        authorBox.setAlignment(Pos.CENTER_LEFT);
        Label authorLabel = new Label("Author: " + post.getAuthor());
        authorLabel.setStyle("-fx-font-style: italic; -fx-font-size: 14;");
        authorBox.getChildren().add(authorLabel);

        // Contenu
        Text contentText = new Text(post.getContent());
        contentText.setWrappingWidth(600);

        // Boutons d'action
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");
        editButton.setOnAction(e -> handleEditPost(post));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeletePost(post));

        Button addCommentButton = new Button("Add comment");
        addCommentButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");
        addCommentButton.setOnAction(e -> handleAddComment(post));

        // Bouton d'export PDF
        Button exportPdfButton = new Button();
        ImageView pdfIcon = new ImageView(new Image(getClass().getResourceAsStream("/pdf-icon.png")));
        pdfIcon.setFitWidth(20);
        pdfIcon.setFitHeight(20);
        exportPdfButton.setGraphic(pdfIcon);
        exportPdfButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        exportPdfButton.setOnAction(e -> exportToPdf(post));
        Tooltip.install(exportPdfButton, new Tooltip("Export to PDF"));

        HBox buttonsContainer = new HBox(10, editButton, deleteButton, addCommentButton, exportPdfButton);
        buttonsContainer.setAlignment(Pos.CENTER_LEFT);

        // Section commentaires
        VBox commentsContainer = new VBox(8);
        commentsContainer.setStyle("-fx-padding: 10 0 0 20;");
        loadCommentsForPost(post, commentsContainer);

        postContainer.getChildren().addAll(titleBox, authorBox, contentText, buttonsContainer, commentsContainer);
        return postContainer;
    }

    private void exportToPdf(Post post) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("post_" + post.getId() + ".pdf");

        File file = fileChooser.showSaveDialog(postsContainer.getScene().getWindow());
        if (file != null) {
            try {
                PdfExporter.exportPostToPdf(post, file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Success", "Post exported successfully to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Export Error", "Failed to export PDF:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadCommentsForPost(Post post, VBox commentsContainer) {
        try {
            commentsContainer.getChildren().clear();
            List<Comment> comments = commentService.getCommentsByPost(post.getId());

            for (Comment comment : comments) {
                VBox commentBox = new VBox(5);
                commentBox.setStyle("-fx-background-color: #e8f5e9; -fx-padding: 10; -fx-border-radius: 5;");

                HBox headerBox = new HBox(10);
                headerBox.setAlignment(Pos.CENTER_LEFT);

                Label authorLabel = new Label(comment.getAuthor());
                authorLabel.setStyle("-fx-font-weight: bold;");

                Label dateLabel = new Label(comment.getCreatedAt().format(dateFormatter));
                dateLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 12;");

                Hyperlink editLink = new Hyperlink("Edit");
                editLink.setStyle("-fx-text-fill: #757575; -fx-underline: true; -fx-font-size: 12;");
                editLink.setOnAction(e -> handleEditComment(comment));

                Label separator = new Label("|");
                separator.setStyle("-fx-text-fill: #757575; -fx-font-size: 12;");

                Hyperlink deleteLink = new Hyperlink("Delete");
                deleteLink.setStyle("-fx-text-fill: #757575; -fx-underline: true; -fx-font-size: 12;");
                deleteLink.setOnAction(e -> handleDeleteComment(comment));

                HBox actionsBox = new HBox(5, editLink, separator, deleteLink);
                headerBox.getChildren().addAll(authorLabel, dateLabel, actionsBox);

                Text commentText = new Text(comment.getContent());
                commentText.setWrappingWidth(550);

                commentBox.getChildren().addAll(headerBox, commentText);
                commentsContainer.getChildren().add(commentBox);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.INFORMATION, "Error", "Failed to load comments: " + e.getMessage());
        }
    }

    private void handleEditComment(Comment comment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateComment.fxml"));
            Parent root = loader.load();
            UpdateCommentController controller = loader.getController();
            controller.setComment(comment);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Comment");
            stage.showAndWait();

            loadPosts();
        } catch (IOException e) {
            showAlert(Alert.AlertType.INFORMATION, "Error", "Cannot open comment editor: " + e.getMessage());
        }
    }

    private void handleDeleteComment(Comment comment) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Delete this comment?");
        confirm.setContentText("This action cannot be undone.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                commentService.deleteComment(comment.getId());
                loadPosts();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.INFORMATION, "Error", "Deletion failed: " + e.getMessage());
            }
        }
    }

    private void handleEditPost(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdatePost.fxml"));
            Parent root = loader.load();
            UpdatePostController controller = loader.getController();
            controller.setPost(post);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Post");
            stage.showAndWait();

            loadPosts();
        } catch (IOException e) {
            showAlert(Alert.AlertType.INFORMATION, "Error", "Cannot open editor: " + e.getMessage());
        }
    }

    private void handleDeletePost(Post post) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Delete this post?");
        confirm.setContentText("This action cannot be undone.");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                postService.deletePost(post.getId());
                loadPosts();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.INFORMATION, "Error", "Deletion failed: " + e.getMessage());
            }
        }
    }

    private void handleAddComment(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddComment.fxml"));
            DialogPane dialogPane = loader.load();
            AddCommentController controller = loader.getController();
            controller.setParentPost(post);

            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("New Comment");

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return controller.handleSubmit();
                }
                return false;
            });

            Optional<Boolean> result = dialog.showAndWait();
            if (result.isPresent() && result.get()) {
                loadPosts();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.INFORMATION, "Error", "Cannot open comment dialog");
        }
    }

    @FXML
    private void handleAddNewPost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddPost.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Post");
            stage.showAndWait();

            loadPosts();
        } catch (IOException e) {
            showAlert(Alert.AlertType.INFORMATION, "Error", "Cannot open form");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            displayPosts(allPosts);
        } else {
            List<Post> filtered = allPosts.stream()
                    .filter(p -> p.getAuthor().toLowerCase().contains(searchText)
                            || p.getContent().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            postsContainer.getChildren().clear();
            displayPosts(filtered);
        }
    }

    private void showAlert(Alert.AlertType information, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

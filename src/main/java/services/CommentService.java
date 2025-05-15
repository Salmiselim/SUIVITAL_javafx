package services;

import models.Comment;
import models.Post;
import outils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentService {

    private Connection connection;

    public CommentService() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    // Créer un nouveau commentaire
    public void createComment(Comment comment) throws SQLException {
        String query = "INSERT INTO comment (content, author, post_id, created_at) VALUES (?, ?, ?, ?)";

        System.out.println("[DEBUG] Inserting comment: " + comment);

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, comment.getContent());
            statement.setString(2, comment.getAuthor());
            statement.setInt(3, comment.getPost().getId());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating comment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getInt(1));
                    System.out.println("[SUCCESS] Comment created with ID: " + comment.getId());
                } else {
                    throw new SQLException("Creating comment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("[SQL ERROR] Failed to create comment: " + e.getMessage());
            throw e;
        }
    }

    // Récupérer tous les commentaires d'un post spécifique
    public List<Comment> getCommentsByPost(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM comment WHERE post_id = ? ORDER BY created_at ASC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Comment comment = new Comment(
                            resultSet.getString("content"),
                            resultSet.getString("author"),
                            new Post() // Post minimal avec juste l'ID
                    );
                    comment.setId(resultSet.getInt("id"));
                    comment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    comment.getPost().setId(postId); // Lie le commentaire à son post
                    comments.add(comment);
                }
            }
        }
        return comments;
    }

    // Mettre à jour un commentaire
    public void updateComment(Comment comment) throws SQLException {
        String query = "UPDATE comment SET author = ?,content = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, comment.getAuthor());
            statement.setString(2, comment.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(4, comment.getId());

            statement.executeUpdate();
        }
    }

    // Supprimer un commentaire
    public void deleteComment(int commentId) throws SQLException {
        String query = "DELETE FROM comment WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, commentId);
            statement.executeUpdate();
        }
    }

    // Récupérer un commentaire par son ID
    public Comment getCommentById(int commentId) throws SQLException {
        String query = "SELECT * FROM comment WHERE id = ?";
        Comment comment = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, commentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    comment = new Comment(
                            resultSet.getString("content"),
                            resultSet.getString("author"),
                            new Post() // Post minimal avec juste l'ID
                    );
                    comment.setId(resultSet.getInt("id"));
                    comment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    comment.getPost().setId(resultSet.getInt("post_id"));
                }
            }
        }
        return comment;
    }

    // Récupérer les commentaires d'un auteur spécifique
    public List<Comment> getCommentsByAuthor(String authorName) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM comment WHERE author = ? ORDER BY created_at DESC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, authorName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Comment comment = new Comment(
                            resultSet.getString("content"),
                            resultSet.getString("author"),
                            new Post()
                    );
                    comment.setId(resultSet.getInt("id"));
                    comment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    comment.getPost().setId(resultSet.getInt("post_id"));
                    comments.add(comment);
                }
            }
        }
        return comments;
    }
}
package services;

import models.Post;
import outils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostService {

    private Connection connection;

    public PostService() {
        this.connection = MyDataBase.getInstance().getConnection();
    }

    // Créer un nouveau post
    public void createPost(Post post) throws SQLException {
        String query = "INSERT INTO post (title, content, author, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setString(3, post.getAuthor());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du post, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création du post, aucun ID obtenu.");
                }
            }
        }
    }

    // Récupérer tous les posts
    public List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM post ORDER BY created_at DESC";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Post post = new Post(
                        resultSet.getString("title"),
                        resultSet.getString("content"),
                        resultSet.getString("author")
                );
                post.setId(resultSet.getInt("id"));
                post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                posts.add(post);
            }
        }
        return posts;
    }

    // Mettre à jour un post
    public void updatePost(Post post) throws SQLException {
        String query = "UPDATE post SET title = ?,author = ?, content = ?, updated_at = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getAuthor());
            statement.setString(3, post.getContent());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(5, post.getId());

            statement.executeUpdate();
        }
    }

    // Supprimer un post
    public void deletePost(int postId) throws SQLException {
        String query = "DELETE FROM post WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, postId);
            statement.executeUpdate();
        }
    }

    // Récupérer un post par son ID
    public Post getPostById(int postId) throws SQLException {
        String query = "SELECT * FROM post WHERE id = ?";
        Post post = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, postId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = new Post(
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getString("author")
                    );
                    post.setId(resultSet.getInt("id"));
                    post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                }
            }
        }
        return post;
    }

    // Récupérer les posts d'un auteur spécifique
    public List<Post> getPostsByAuthor(String authorName) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM post WHERE author = ? ORDER BY created_at DESC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, authorName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Post post = new Post(
                            resultSet.getString("title"),
                            resultSet.getString("content"),
                            resultSet.getString("author")
                    );
                    post.setId(resultSet.getInt("id"));
                    post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    posts.add(post);
                }
            }
        }
        return posts;
    }
}
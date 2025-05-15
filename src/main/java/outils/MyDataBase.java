package outils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private final String URL = "jdbc:mysql://localhost:3306/projectpi";
    private final String USER = "root";
    private final String PASSWORD = "";

    private Connection conn;
    private static MyDataBase instance;

    private MyDataBase() {
        try {
            // 1. Chargement du pilote
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Établissement de la connexion
            this.conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[SUCCÈS] Connexion à la base de données établie");

        } catch (ClassNotFoundException e) {
            System.err.println("[ERREUR] Pilote JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[ERREUR] Échec de connexion : " + e.getMessage());
            // Gestion supplémentaire si besoin (ex: relancer l'application)
        }
    }

    public static synchronized MyDataBase getInstance() {
        if (instance == null) {
            instance = new MyDataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                reconnect();
            }
        } catch (SQLException e) {
            System.err.println("[ERREUR] Vérification connexion : " + e.getMessage());
        }
        return conn;
    }

    private void reconnect() throws SQLException {
        this.conn = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Méthode pour fermer la connexion (à appeler à la fermeture de l'app)
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("[INFO] Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("[ERREUR] Fermeture connexion : " + e.getMessage());
        }
    }
}
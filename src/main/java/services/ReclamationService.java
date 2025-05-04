package services;

import models.Reclamation;
import outils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {
    private Connection connection;

    public ReclamationService() {
        connection = MyDataBase.getInstance().getConn();
    }

    public void ajouter(Reclamation reclamation) {
        String sql = "INSERT INTO reclamation (user_id, objet, commentaire, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, 1); // Hardcoded user_id
            stmt.setString(2, reclamation.getObjet());
            stmt.setString(3, reclamation.getCommentaire());
            stmt.setDate(4, Date.valueOf(reclamation.getDate())); // Ensure the 'date' field is of type LocalDate
            stmt.executeUpdate();
            System.out.println("Reclamation ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reclamation> afficher() {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT id, user_id, objet, commentaire, date FROM reclamation"; // Fixed SELECT query
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getString("objet"),
                        rs.getString("commentaire"),
                        rs.getDate("date").toLocalDate()
                );
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reclamation WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void modifier(Reclamation reclamation) {
        String sql = "UPDATE reclamation SET objet = ?, commentaire = ?, date = ? WHERE id = ?"; // Fixed column names
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reclamation.getObjet());
            stmt.setString(2, reclamation.getCommentaire());
            stmt.setDate(3, Date.valueOf(reclamation.getDate())); // Ensure correct date format
            stmt.setInt(4, reclamation.getId());
            stmt.executeUpdate();
            System.out.println("Reclamation modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
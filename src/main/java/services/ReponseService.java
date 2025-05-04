package services;

import models.Reponse;
import outils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService {
    private Connection connection;

    public ReponseService() {
        connection = MyDataBase.getInstance().getConn();
    }

    public void ajouter(Reponse reponse) {
        String sql = "INSERT INTO reponse (user_id, objet, commentaire, date, reclamation_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reponse.getUserId());
            stmt.setString(2, reponse.getObjet());
            stmt.setString(3, reponse.getCommentaire());
            stmt.setDate(4, Date.valueOf(reponse.getDate()));
            stmt.setInt(5, reponse.getReclamationId());
            stmt.executeUpdate();
            System.out.println("Réponse ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reponse> afficher() {
        List<Reponse> list = new ArrayList<>();
        String sql = "SELECT id, user_id, objet, commentaire, date, reclamation_id FROM reponse";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reponse r = new Reponse(
                        rs.getInt("id"),
                        rs.getString("objet"),
                        rs.getString("commentaire"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("reclamation_id")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM reponse WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void modifier(Reponse reponse) {
        String sql = "UPDATE reponse SET objet = ?, commentaire = ?, date = ?, reclamation_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reponse.getObjet());
            stmt.setString(2, reponse.getCommentaire());
            stmt.setDate(3, Date.valueOf(reponse.getDate()));
            stmt.setInt(4, reponse.getReclamationId());
            stmt.setInt(5, reponse.getId());
            stmt.executeUpdate();
            System.out.println("Réponse modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package services;

import models.Medicament;
import outils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedService implements Crud<Medicament> {
    Connection conn;

    public MedService() {
        this.conn = MyDataBase.getInstance().getConn();
    }

    @Override
    public void create(Medicament obj) throws Exception {
        String sql = "INSERT INTO medicament (name, dosage, duration, frequency) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, obj.getName());
        stmt.setString(2, obj.getDosage());
        stmt.setInt(3, obj.getDuration());
        stmt.setString(4, obj.getFrequency());
        stmt.executeUpdate();
    }

    @Override
    public void update(Medicament obj) throws Exception {
        String sql = "UPDATE medicament SET name = ?, dosage = ?, duration = ?, frequency = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, obj.getName());
        stmt.setString(2, obj.getDosage());
        stmt.setInt(3, obj.getDuration());
        stmt.setString(4, obj.getFrequency());
        stmt.setInt(5, obj.getId());
        stmt.executeUpdate();
    }
    @Override
    public void delete(int id) {
        try {
            String sql = "DELETE FROM medicament WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Medicament> getAll() throws Exception {
        String sql = "SELECT * FROM medicament";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Medicament> medicaments = new ArrayList<>();
        while (rs.next()) {
            Medicament med = new Medicament();
            med.setId(rs.getInt("id"));
            med.setName(rs.getString("name"));
            med.setDosage(rs.getString("dosage"));
            med.setDuration(rs.getInt("duration"));
            med.setFrequency(rs.getString("frequency"));
            medicaments.add(med);
        }
        return medicaments;
    }
}

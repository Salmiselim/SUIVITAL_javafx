package services;

import models.Medicament;
import outils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedService implements Crud<Medicament> {
    Connection conn;
    private List<Medicament> medicaments = new ArrayList<>();

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

    public Medicament getByName(String name) {
        try {
            String sql = "SELECT * FROM medicament WHERE LOWER(name) = LOWER(?) LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Medicament med = new Medicament();
                med.setId(rs.getInt("id"));
                med.setName(rs.getString("name"));
                med.setDosage(rs.getString("dosage"));
                med.setDuration(rs.getInt("duration"));
                med.setFrequency(rs.getString("frequency"));
                return med;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void populateTestData() {
        try {
            // Clear existing data
            String clearSql = "DELETE FROM medicament";
            Statement clearStmt = conn.createStatement();
            clearStmt.executeUpdate(clearSql);

            // Add test medications
            String[] testMeds = {
                "INSERT INTO medicament (name, dosage, duration, frequency) VALUES ('Paracétamol', '500mg', 7, '3 fois par jour')",
                "INSERT INTO medicament (name, dosage, duration, frequency) VALUES ('Ibuprofène', '400mg', 5, '2 fois par jour')",
                "INSERT INTO medicament (name, dosage, duration, frequency) VALUES ('Sirop antitussif', '10ml', 5, '3 fois par jour')",
                "INSERT INTO medicament (name, dosage, duration, frequency) VALUES ('Pastilles pour la gorge', '1 comprimé', 3, '4 fois par jour')",
                "INSERT INTO medicament (name, dosage, duration, frequency) VALUES ('Aspirine', '500mg', 3, '2 fois par jour')",
                "INSERT INTO medicament (name, dosage, duration, frequency) VALUES ('Diclofénac', '50mg', 5, '2 fois par jour')"
            };

            Statement stmt = conn.createStatement();
            for (String sql : testMeds) {
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

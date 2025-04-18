package services;

import models.Medicament;
import models.Ordonnance;
import outils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceService implements Crud<Ordonnance> {
    Connection conn;

    public OrdonnanceService() {
        this.conn = MyDataBase.getInstance().getConn();
    }

    @Override
    public void create(Ordonnance obj) throws Exception {
        String sql = "INSERT INTO ordonnance (description, date_prescription) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, obj.getDescription());
        stmt.setTimestamp(2, new Timestamp(obj.getDatePrescription().getTime()));
        stmt.executeUpdate();

        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            int ordonnanceId = generatedKeys.getInt(1);
            obj.setId(ordonnanceId);
            insertOrdonnanceMedicaments(ordonnanceId, obj.getMedicaments());
        }
    }

    @Override
    public void update(Ordonnance obj) throws Exception {
        String sql = "UPDATE ordonnance SET description = ?, date_prescription = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, obj.getDescription());
        stmt.setTimestamp(2, new Timestamp(obj.getDatePrescription().getTime()));
        stmt.setInt(3, obj.getId());
        stmt.executeUpdate();

        // Update join table
        deleteOrdonnanceMedicaments(obj.getId());
        insertOrdonnanceMedicaments(obj.getId(), obj.getMedicaments());
    }

    @Override
    public void delete(int id) throws Exception {
        deleteOrdonnanceMedicaments(id);
        String sql = "DELETE FROM ordonnance WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }
    @Override
    public List<Ordonnance> getAll() throws Exception {
        String sql = "SELECT * FROM ordonnance";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<Ordonnance> list = new ArrayList<>();

        while (rs.next()) {
            Ordonnance o = new Ordonnance();
            o.setId(rs.getInt("id"));
            o.setDescription(rs.getString("description"));
            o.setDatePrescription(rs.getTimestamp("date_prescription"));
            o.setMedicaments(getMedicamentsByOrdonnanceId(o.getId()));
            list.add(o);
        }
        return list;
    }

    private void insertOrdonnanceMedicaments(int ordonnanceId, List<Medicament> meds) throws SQLException {
        String sql = "INSERT INTO ordonnance_medicament (ordonnance_id, medicament_id) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (Medicament m : meds) {
            stmt.setInt(1, ordonnanceId);
            stmt.setInt(2, m.getId());
            stmt.addBatch();
        }
        stmt.executeBatch();
    }

    private void deleteOrdonnanceMedicaments(int ordonnanceId) throws SQLException {
        String sql = "DELETE FROM ordonnance_medicament WHERE ordonnance_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, ordonnanceId);
        stmt.executeUpdate();
    }

    private List<Medicament> getMedicamentsByOrdonnanceId(int ordonnanceId) throws SQLException {
        String sql = """
            SELECT m.* FROM medicament m
            JOIN ordonnance_medicament om ON m.id = om.medicament_id
            WHERE om.ordonnance_id = ?
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, ordonnanceId);
        ResultSet rs = stmt.executeQuery();
        List<Medicament> meds = new ArrayList<>();
        while (rs.next()) {
            Medicament med = new Medicament();
            med.setId(rs.getInt("id"));
            med.setName(rs.getString("name"));
            med.setDosage(rs.getString("dosage"));
            med.setDuration(rs.getInt("duration"));
            med.setFrequency(rs.getString("frequency"));
            meds.add(med);
        }
        return meds;
    }
}

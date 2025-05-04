package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ordonnance {
    private int id;
    private int patientId;
    private int doctorId;
    private String description;
    private Date datePrescription;
    private List<Medicament> medicaments = new ArrayList<>();

    public Ordonnance() {}

    public Ordonnance(int id, int patientId, int doctorId, String description, Date datePrescription, List<Medicament> medicaments) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.description = description;
        this.datePrescription = datePrescription;
        this.medicaments = medicaments;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDatePrescription() { return datePrescription; }
    public void setDatePrescription(Date datePrescription) { this.datePrescription = datePrescription; }

    public List<Medicament> getMedicaments() { return medicaments; }
    public void setMedicaments(List<Medicament> medicaments) { this.medicaments = medicaments; }
}

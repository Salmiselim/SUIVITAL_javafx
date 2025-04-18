package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ordonnance {
    private int id;
    private String description;
    private Date datePrescription;
    private List<Medicament> medicaments = new ArrayList<>();

    public Ordonnance() {}

    public Ordonnance(int id, String description, Date datePrescription, List<Medicament> medicaments) {
        this.id = id;
        this.description = description;
        this.datePrescription = datePrescription;
        this.medicaments = medicaments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatePrescription() {
        return datePrescription;
    }

    public void setDatePrescription(Date datePrescription) {
        this.datePrescription = datePrescription;
    }

    public List<Medicament> getMedicaments() {
        return medicaments;
    }

    public void setMedicaments(List<Medicament> medicaments) {
        this.medicaments = medicaments;
    }
}

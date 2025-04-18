package models;

import java.util.ArrayList;
import java.util.List;

public class Medicament {
    private int id;
    private String name;
    private String dosage;
    private int duration;
    private String frequency;

    private List<Ordonnance> ordonnances = new ArrayList<>();

    public Medicament() {}

    public Medicament(String name, String dosage, int duration, String frequency) {
        this.name = name;
        this.dosage = dosage;
        this.duration = duration;
        this.frequency = frequency;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public List<Ordonnance> getOrdonnances() { return ordonnances; }
    public void addOrdonnance(Ordonnance ordonnance) {
        if (!ordonnances.contains(ordonnance)) {
            ordonnances.add(ordonnance);
            ordonnance.getMedicaments().add(this);
        }
    }

    public void removeOrdonnance(Ordonnance ordonnance) {
        ordonnances.remove(ordonnance);
        ordonnance.getMedicaments().remove(this);
    }
}

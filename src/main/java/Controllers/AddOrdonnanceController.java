package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Medicament;
import models.Ordonnance;
import services.MedService;
import services.OrdonnanceService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddOrdonnanceController {

    @FXML
    private TextArea descriptionField;
    @FXML
    private MenuButton medDropdown;

    private final MedService medService = new MedService();
    private final OrdonnanceService ordonnanceService = new OrdonnanceService();
    private List<Medicament> allMeds = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private List<Medicament> suggestedMedications = new ArrayList<>();
    private Runnable onOrdonnanceAdded;

    @FXML
    public void initialize() {
        try {
            // Load medications
            if (!suggestedMedications.isEmpty()) {
                allMeds = suggestedMedications;
            } else {
                allMeds = medService.getAll();
            }
            
            // Clear existing items
            medDropdown.getItems().clear();
            checkBoxes.clear();
            
            // Add medications to dropdown
            for (Medicament med : allMeds) {
                CheckBox cb = new CheckBox(med.getName());
                cb.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-text-fill: #2c3e50;" +
                    "-fx-padding: 2 8;" +
                    "-fx-background-radius: 2;" +
                    "-fx-border-radius: 2;" +
                    "-fx-background-color: transparent;" +
                    "-fx-border-color: #e0e0e0;" +
                    "-fx-border-width: 1;" +
                    "-fx-cursor: hand;"
                );
                // Pre-select if in suggestedMedications
                if (!suggestedMedications.isEmpty() && suggestedMedications.stream().anyMatch(s -> s.getName().equalsIgnoreCase(med.getName()))) {
                    cb.setSelected(true);
                }
                CustomMenuItem item = new CustomMenuItem(cb);
                item.setHideOnClick(false);
                medDropdown.getItems().add(item);
                checkBoxes.add(cb);
            }
            
            // Set default text
            medDropdown.setText("Sélectionner Médicaments (" + allMeds.size() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des médicaments: " + e.getMessage()).showAndWait();
        }
    }

    public void setOnOrdonnanceAdded(Runnable callback) {
        this.onOrdonnanceAdded = callback;
    }

    @FXML
    private void handleSave() {
        try {
            String desc = descriptionField.getText().trim();

            if (desc.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Champ requis");
                alert.setHeaderText(null);
                alert.setContentText("Vous devez entrer une description !");
                alert.showAndWait();
                return;
            }

            Date now = new Date();

            List<Medicament> selected = new ArrayList<>();
            for (int i = 0; i < allMeds.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selected.add(allMeds.get(i));
                }
            }

            Ordonnance ord = new Ordonnance();
            ord.setDescription(desc);
            ord.setDatePrescription(now);
            ord.setMedicaments(selected);

            ordonnanceService.create(ord);

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Ordonnance ajoutée avec succès !", ButtonType.OK);
            alert.showAndWait();

            // Refresh the ordonnance list in the main window
            if (onOrdonnanceAdded != null) {
                onOrdonnanceAdded.run();
            }

            // Return to the main ordonnance scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayOrd.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) descriptionField.getScene().getWindow();
            stage.setTitle("Gestion des Ordonnances");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erreur : " + e.getMessage(), ButtonType.OK)
                    .showAndWait();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayOrd.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Liste des Ordonnances");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erreur de navigation : Impossible de charger DisplayOrd.fxml", ButtonType.OK)
                    .showAndWait();
        }
    }

    public void setSuggestedMedications(List<Medicament> medications) {
        this.suggestedMedications = medications;
    }
}

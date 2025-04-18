package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Medicament;
import services.MedService;

import java.util.List;

public class DisplayMedController {

    @FXML
    private VBox medicamentContainer;

    private final MedService medService = new MedService();

    @FXML
    public void initialize() {
        loadMedicaments();
    }

    private void loadMedicaments() {
        medicamentContainer.getChildren().clear();

        try {
            List<Medicament> meds = medService.getAll();

            for (Medicament med : meds) {
                HBox medBox = new HBox(20);
                medBox.setStyle(
                        "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-border-color: #cccccc; " +
                        "-fx-padding: 12; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                );
                medBox.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label("💊 " + med.getName());
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label dosageLabel = new Label("Dosage: " + med.getDosage());
                dosageLabel.setStyle("-fx-text-fill: #444444;");

                Label frequencyLabel = new Label("Frequency: " + med.getFrequency());
                Label durationLabel = new Label("Duration: " + med.getDuration() + " days");

                Button deleteButton = new Button("Delete");
                deleteButton.setStyle(
                        "-fx-background-color: #ff4d4d; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
                );

                deleteButton.setOnAction(event -> {
                    medService.delete(med.getId());
                    loadMedicaments();               // Refresh UI
                });

                // Edit Button
                Button editButton = new Button("Edit");
                editButton.setStyle(
                        "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold;"
                );

                editButton.setOnAction(ev -> openEditDialog(med));

                medBox.getChildren().addAll(
                        nameLabel, dosageLabel, frequencyLabel, durationLabel, editButton, deleteButton
                );

                medicamentContainer.getChildren().add(medBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditDialog(Medicament med) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditMed.fxml"));
            Parent root = loader.load();

            EditMedController controller = loader.getController();
            controller.setMed(med);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Medicament");
            stage.showAndWait();

            loadMedicaments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

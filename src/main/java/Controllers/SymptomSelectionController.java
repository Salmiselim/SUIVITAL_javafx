package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.AIService;
import services.MedService;
import models.Medicament;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymptomSelectionController {

    @FXML private VBox symptomsContainer;
    @FXML private VBox suggestionsContainer;
    @FXML private VBox medicationsContainer;

    private final AIService aiService = new AIService();
    private final MedService medService = new MedService();
    private List<CheckBox> symptomCheckboxes = new ArrayList<>();
    private List<Medicament> suggestedMedications = new ArrayList<>();

    @FXML
    public void initialize() {
        String[] commonSymptoms = {
            "Fièvre", "Toux", "Maux de tête", "Douleurs musculaires",
            "Fatigue", "Nausées", "Douleurs abdominales", "Diarrhée",
            "Congestion nasale", "Mal de gorge", "Éternuements",
            "Douleurs articulaires", "Vertiges", "Insomnie"
        };

        for (String symptom : commonSymptoms) {
            CheckBox checkBox = new CheckBox(symptom);
            checkBox.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #2c3e50;
                -fx-padding: 5;
            """);
            symptomCheckboxes.add(checkBox);
            symptomsContainer.getChildren().add(checkBox);
        }
    }

    @FXML
    private void handleGetSuggestions() {
        List<String> selectedSymptoms = symptomCheckboxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .collect(Collectors.toList());

        if (selectedSymptoms.isEmpty()) {
            showAlert("Avertissement", "Veuillez sélectionner au moins un symptôme.");
            return;
        }

        try {
            // Get AI suggestions
            List<String> suggestedMedNames = aiService.getMedicationSuggestions(selectedSymptoms);
            
            // Clear previous suggestions
            medicationsContainer.getChildren().clear();
            suggestedMedications.clear();

            // Get medication details from database
            for (String medName : suggestedMedNames) {
                Medicament med = medService.getByName(medName);
                if (med != null) {
                    suggestedMedications.add(med);
                    addMedicationCard(med);
                }
            }

            // Show suggestions section
            suggestionsContainer.setVisible(true);
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la génération des suggestions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addMedicationCard(Medicament med) {
        VBox card = new VBox(10);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #e9ecef;
            -fx-border-radius: 10;
            -fx-padding: 15;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);
        """);

        Label name = new Label("💊 " + med.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label dosage = new Label("📏 Dosage: " + med.getDosage());
        dosage.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label frequency = new Label("⏰ Fréquence: " + med.getFrequency());
        frequency.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        card.getChildren().addAll(name, dosage, frequency);
        medicationsContainer.getChildren().add(card);
    }

    @FXML
    private void handleContinue() {
        if (suggestedMedications.isEmpty()) {
            showAlert("Avertissement", "Veuillez d'abord obtenir des suggestions de médicaments.");
            return;
        }

        try {
            // Open AddOrd with suggested medications
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddOrd.fxml"));
            Parent root = loader.load();

            AddOrdonnanceController controller = loader.getController();
            controller.setSuggestedMedications(suggestedMedications);

            Stage stage = (Stage) symptomsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page suivante.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) symptomsContainer.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
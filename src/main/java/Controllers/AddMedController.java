package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import models.Medicament;
import services.MedService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AddMedController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> dosageField;
    @FXML private TextField durationField;
    @FXML private ComboBox<String> frequencyField;

    private final MedService medService = new MedService();

    @FXML
    public void initialize() {
        // Initialize frequency options
        frequencyField.getItems().addAll(
            "Une fois par jour",
            "Deux fois par jour",
            "Trois fois par jour"
        );

        // Initialize dosage options
        dosageField.getItems().addAll(
            "1g",
            "250mg",
            "500mg",
            "100mg"
        );

        durationField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char ch = event.getCharacter().charAt(0);
            if (!Character.isDigit(ch)) {
                event.consume();
            }
        });
    }

    @FXML
    public void handleAdd() {
        try {
            String name = nameField.getText().trim();
            String dosage = dosageField.getValue();
            String durationText = durationField.getText().trim();
            String frequency = frequencyField.getValue();

            if (name.isEmpty()) {
                showAlert("Validation Error", "Vous devez entrer un nom.");
                return;
            }

            if (dosage == null || dosage.isEmpty()) {
                showAlert("Validation Error", "Vous devez sélectionner un dosage.");
                return;
            }

            if (durationText.isEmpty()) {
                showAlert("Validation Error", "Vous devez entrer une durée.");
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(durationText);
                if (duration <= 0) {
                    showAlert("Validation Error", "La durée doit être supérieure à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Validation Error", "La durée doit être un nombre valide.");
                return;
            }

            if (frequency == null || frequency.isEmpty()) {
                showAlert("Validation Error", "Vous devez sélectionner une fréquence.");
                return;
            }

            Medicament med = new Medicament(name, dosage, duration, frequency);
            medService.create(med);

            showAlert("Succès", "Médicament ajouté avec succès!");
            clearForm();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nameField.clear();
        dosageField.getSelectionModel().clearSelection();
        durationField.clear();
        frequencyField.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayMed.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur de navigation", "Impossible de charger DisplayMed.fxml");
            e.printStackTrace();
        }
    }
}

package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import models.Medicament;
import services.MedService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class AddMedController {

    @FXML
    private TextField txtName;

    @FXML
    private ComboBox<String> txtDosage;

    @FXML
    private TextField txtDuration;

    @FXML
    private ComboBox<String> txtFrequency;

    @FXML
    private Button btnAdd;

    private final MedService medService = new MedService();

    @FXML
    public void initialize() {
        txtFrequency.getItems().addAll(
                "Once a day",
                "Two times a day",
                "Three times a day"
        );

        txtDosage.getItems().addAll(
                "1g",
                "250mg",
                "500mg",
                "100mg"
        );

        btnAdd.setOnAction(event -> {
            try {
                addMedicament();
            } catch (Exception e) {
                showAlert("Error", e.getMessage());
                e.printStackTrace();
            }
        });

        txtDuration.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char ch = event.getCharacter().charAt(0);

            if (!Character.isDigit(ch)) {
                event.consume();
            }
        });
    }

    @FXML
    public void onFrequencyChange() {
        String selected = txtFrequency.getValue();
        System.out.println("Selected frequency: " + selected);
    }

    @FXML
    public void onDosageChange() {
        String selected = txtDosage.getValue();
        System.out.println("Selected dosage: " + selected);
    }

    @FXML
    public void addMedicament() throws Exception {
        String name = txtName.getText();
        String dosage = txtDosage.getValue();
        int duration = Integer.parseInt(txtDuration.getText());
        String frequency = txtFrequency.getValue();

        if (name.isEmpty() || dosage == null || frequency == null || duration <= 0) {
            showAlert("Validation Error", "Please fill all fields correctly.");
            return;
        }

        Medicament med = new Medicament(name, dosage, duration, frequency);
        medService.create(med);

        showAlert("Success", "Medicament added successfully!");

        clearForm();
    }

    private void clearForm() {
        txtName.clear();
        txtDuration.clear();
        txtFrequency.getSelectionModel().clearSelection();
        txtDosage.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goToDisplayMed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayMed.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not load DisplayMed.fxml");
            e.printStackTrace();
        }
    }
}

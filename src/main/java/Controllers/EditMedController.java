package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import models.Medicament;
import services.MedService;
import javafx.scene.input.KeyEvent;

public class EditMedController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> dosageField;
    @FXML private TextField durationField;
    @FXML private ComboBox<String> frequencyField;

    private final MedService medService = new MedService();
    private Medicament med;

    public void setMed(Medicament med) {
        this.med = med;
        nameField.setText(med.getName());
        dosageField.setValue(med.getDosage());
        durationField.setText(String.valueOf(med.getDuration()));
        frequencyField.setValue(med.getFrequency());
    }

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
    public void handleSave() {
        try {
            if (nameField.getText().isEmpty() || dosageField.getValue() == null ||
                durationField.getText().isEmpty() || frequencyField.getValue() == null) {
                System.out.println("Veuillez remplir tous les champs.");
                return;
            }

            med.setName(nameField.getText());
            med.setDosage(dosageField.getValue());
            med.setDuration(Integer.parseInt(durationField.getText()));
            med.setFrequency(frequencyField.getValue());

            medService.update(med);
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.out.println("La durée doit être un nombre.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}

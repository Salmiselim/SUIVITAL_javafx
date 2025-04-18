package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Medicament;
import services.MedService;
import javafx.scene.input.KeyEvent;

public class EditMedController {

    @FXML private TextField txtName;
    @FXML private ComboBox<String> txtDosage;
    @FXML private TextField txtDuration;
    @FXML private ComboBox<String> txtFrequency;

    private final MedService medService = new MedService();
    private Medicament med;

    public void setMed(Medicament med) {
        this.med = med;
        txtName.setText(med.getName());
        txtDosage.setValue(med.getDosage());
        txtDuration.setText(String.valueOf(med.getDuration()));
        txtFrequency.setValue(med.getFrequency());
    }

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

        txtDuration.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char ch = event.getCharacter().charAt(0);
            if (!Character.isDigit(ch)) {
                event.consume();
            }
        });
    }

    @FXML
    public void saveMed() {
        try {
            if (txtName.getText().isEmpty() || txtDosage.getValue() == null ||
                txtDuration.getText().isEmpty() || txtFrequency.getValue() == null) {
                System.out.println("Please fill all fields.");
                return;
            }

            med.setName(txtName.getText());
            med.setDosage(txtDosage.getValue());
            med.setDuration(Integer.parseInt(txtDuration.getText()));
            med.setFrequency(txtFrequency.getValue());

            medService.update(med);
            Stage stage = (Stage) txtName.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.out.println("Duration must be a number.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

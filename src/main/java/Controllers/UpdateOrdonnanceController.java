package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Ordonnance;
import services.OrdonnanceService;

import java.time.ZoneId;
import java.util.Date;
import java.util.function.Consumer;

public class UpdateOrdonnanceController {

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker datePicker;

    private Ordonnance ordonnance;
    private final OrdonnanceService ordonnanceService = new OrdonnanceService();
    private Consumer<Void> onUpdateCallback;

    public void setOrdonnance(Ordonnance ordonnance) {
        this.ordonnance = ordonnance;
        descriptionField.setText(ordonnance.getDescription());
        datePicker.setValue(ordonnance.getDatePrescription().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public void setOnUpdate(Consumer<Void> callback) {
        this.onUpdateCallback = callback;
    }

    @FXML
    private void handleUpdate() {
        try {
            ordonnance.setDescription(descriptionField.getText());
            ordonnance.setDatePrescription(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            ordonnanceService.update(ordonnance);

            if (onUpdateCallback != null) {
                onUpdateCallback.accept(null);
            }

            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour !");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) descriptionField.getScene().getWindow()).close();
    }
}

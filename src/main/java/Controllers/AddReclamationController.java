package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Reclamation;
import services.ReclamationService;
import services.EmailService;

import java.time.LocalDate;
import java.sql.SQLException;
import javafx.stage.Stage;

public class AddReclamationController {

    @FXML
    private TextField tfSujet;

    @FXML
    private TextArea taDescription;

    @FXML
    private Label lblDate;

    private final ReclamationService service = new ReclamationService();

    @FXML
    public void initialize() {
        lblDate.setText("Date de réclamation : " + LocalDate.now().toString());
    }

    @FXML
    private void onSave() {
        String sujet = tfSujet.getText();
        String description = taDescription.getText();

        if (sujet.isEmpty() || description.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Tous les champs doivent être remplis !");
            alert.show();
            return;
        }

        LocalDate date = LocalDate.now();

        // Creating Reclamation with hardcoded userId (1)
        Reclamation reclamation = new Reclamation(0, sujet, description, date);
        service.ajouter(reclamation);

        // Send email notification
        EmailService.sendNewReclamationNotification();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réclamation ajoutée !");
        alert.show();

        // Clear fields
        tfSujet.clear();
        taDescription.clear();
    }

    @FXML
    private void onAdd() {
        String sujet = tfSujet.getText();
        String description = taDescription.getText();
        LocalDate date = LocalDate.now();

        // Create new reclamation
        Reclamation reclamation = new Reclamation(0, sujet, description, date);

        // Add to database
        service.ajouter(reclamation);
        
        // Send email notification
        EmailService.sendNewReclamationNotification();
        
        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Réclamation ajoutée avec succès !");
        alert.showAndWait();
        
        // Close the window
        Stage stage = (Stage) tfSujet.getScene().getWindow();
        stage.close();
    }
}
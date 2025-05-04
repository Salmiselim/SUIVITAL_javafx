package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Reponse;
import services.ReponseService;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddReponseController {

    @FXML
    private TextField tfObjet;

    @FXML
    private TextArea taCommentaire;

    @FXML
    private TextArea taDescription;

    @FXML
    private Label lblDate;  // Label for "Date de la réponse"

    @FXML
    private Label lblDateValue;  // Label to display the actual date (read-only)

    private int reclamationId = -1; // Default value indicating no reclamation selected
    private final ReponseService service = new ReponseService();

    public void setReclamationId(int id) {
        this.reclamationId = id;
    }

    @FXML
    public void initialize() {
        lblDate.setText("Date de réponse : " + LocalDate.now().toString());
    }

    @FXML
    private void onSave() {
        String objet = tfObjet.getText();
        String commentaire = taCommentaire.getText();

        if (objet.isEmpty() || commentaire.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Tous les champs doivent être remplis !");
            alert.show();
            return;
        }

        if (reclamationId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Aucune réclamation sélectionnée !");
            alert.show();
            return;
        }

        LocalDate date = LocalDate.now();

        // Creating Reponse with the reclamation ID
        Reponse reponse = new Reponse(0, objet, commentaire, date, reclamationId);
        service.ajouter(reponse);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réponse ajoutée !");
        alert.show();

        // Close the window
        Stage stage = (Stage) tfObjet.getScene().getWindow();
        stage.close();
    }
}
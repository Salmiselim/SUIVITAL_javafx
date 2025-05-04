package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Reclamation;
import services.ReclamationService;

public class EditReclamationController {

    @FXML
    private TextField tfObjet;

    @FXML
    private TextArea taCommentaire;

    @FXML
    private Label lblDate;

    private Reclamation reclamation;
    private final ReclamationService service = new ReclamationService();

    // Initialize this controller with the data from the selected reclamation
    public void initData(Reclamation reclamation) {
        this.reclamation = reclamation;
        tfObjet.setText(reclamation.getObjet());
        taCommentaire.setText(reclamation.getCommentaire());
        lblDate.setText("Date: " + reclamation.getDate().toString());
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

        reclamation.setObjet(objet);
        reclamation.setCommentaire(commentaire);

        service.modifier(reclamation);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réclamation modifiée !");
        alert.show();

        // Close the edit window
        Stage stage = (Stage) tfObjet.getScene().getWindow();
        stage.close();
    }
}

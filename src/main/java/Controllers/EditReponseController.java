package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Reponse;
import services.ReponseService;

public class EditReponseController {

    @FXML
    private TextField tfObjet;

    @FXML
    private TextArea taCommentaire;

    @FXML
    private Label lblDate;

    private Reponse reponse;
    private final ReponseService service = new ReponseService();

    // Initialize this controller with the data from the selected response
    public void initData(Reponse reponse) {
        this.reponse = reponse;
        tfObjet.setText(reponse.getObjet());
        taCommentaire.setText(reponse.getCommentaire());
        lblDate.setText("Date: " + reponse.getDate().toString());
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

        reponse.setObjet(objet);
        reponse.setCommentaire(commentaire);

        service.modifier(reponse);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réponse modifiée !");
        alert.show();

        // Close the edit window
        Stage stage = (Stage) tfObjet.getScene().getWindow();
        stage.close();
    }
}
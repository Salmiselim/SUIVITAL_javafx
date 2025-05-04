package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Medicament;
import services.MedService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.List;

public class DisplayMedController {

    @FXML
    private VBox medicamentContainer;
    
    @FXML
    private TextField searchField;

    private final MedService medService = new MedService();

    @FXML
    public void initialize() {
        loadMedicaments();
        
        // Add search listener
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchMedicaments(newValue);
            }
        });
    }

    private void searchMedicaments(String searchText) {
        medicamentContainer.getChildren().clear();

        try {
            List<Medicament> meds = medService.getAll();
            
            for (Medicament med : meds) {
                if (searchText.isEmpty() || 
                    med.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    med.getDosage().toLowerCase().contains(searchText.toLowerCase()) ||
                    med.getFrequency().toLowerCase().contains(searchText.toLowerCase())) {
                    
                    medicamentContainer.getChildren().add(createCard(med));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(Medicament med) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #e9ecef;
            -fx-border-radius: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);
            """);

        // Name
        Label name = new Label("💊 " + med.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        name.setWrapText(true);

        // Dosage
        Label dosage = new Label("📏 Dosage: " + med.getDosage());
        dosage.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Frequency
        Label frequency = new Label("⏰ Fréquence: " + med.getFrequency());
        frequency.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Duration
        Label duration = new Label("📅 Durée: " + med.getDuration() + " jours");
        duration.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button edit = new Button("Modifier");
        Button delete = new Button("Supprimer");

        String buttonStyle = """
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-padding: 8 15;
            -fx-background-radius: 25;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);
            """;

        edit.setStyle(buttonStyle + "-fx-background-color: #f39c12; -fx-text-fill: white;");
        delete.setStyle(buttonStyle + "-fx-background-color: #e74c3c; -fx-text-fill: white;");

        edit.setOnAction(e -> openEditDialog(med));
        delete.setOnAction(e -> confirmAndDelete(med));

        actions.getChildren().addAll(edit, delete);
        card.getChildren().addAll(name, dosage, frequency, duration, actions);

        return card;
    }

    private void confirmAndDelete(Medicament med) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer ce médicament ?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK) {
                try {
                    medService.delete(med.getId());
                    loadMedicaments();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,
                            "Erreur: " + ex.getMessage(), ButtonType.OK).showAndWait();
                }
            }
        });
    }

    private void loadMedicaments() {
        searchMedicaments(""); // Load all medicaments
    }

    private void openEditDialog(Medicament med) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditMed.fxml"));
            Parent root = loader.load();

            EditMedController controller = loader.getController();
            controller.setMed(med);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Médicament");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadMedicaments(); // Refresh after editing
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAddMed() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMed.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter Médicament");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadMedicaments(); // Refresh after adding
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reclamation;
import services.ReclamationService;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;

public class DisplayReclamationController {

    @FXML
    private TableView<Reclamation> tableViewReclamations;

    @FXML
    private TableColumn<Reclamation, Integer> colId;

    @FXML
    private TableColumn<Reclamation, String> colObjet;

    @FXML
    private TableColumn<Reclamation, String> colMessage;

    @FXML
    private TableColumn<Reclamation, String> colDate;

    @FXML
    private TextField searchField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button btnVoirReponses;

    private final ReclamationService service = new ReclamationService();
    private List<Reclamation> allReclamations;

    @FXML
    public void onVoirReponses() {

        try {
            // Load the DisplayReponse.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DisplayReponse.fxml"));
            Scene scene = new Scene(loader.load());

            // Create a new stage (window)
            Stage stage = new Stage();
            stage.setTitle("Réponses");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la page des réponses !");
            alert.show();
        }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colObjet.setCellValueFactory(new PropertyValueFactory<>("objet"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Set DatePicker format to YYYY-MM-DD
        datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            private final java.time.format.DateTimeFormatter dateFormatter = 
                java.time.format.DateTimeFormatter.ISO_DATE;

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        
        // Initialize search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterReclamations();
        });
        
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterReclamations();
        });
        
        // Add double-click handler
        tableViewReclamations.setRowFactory(tv -> {
            TableRow<Reclamation> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Reclamation selectedReclamation = row.getItem();
                    openDetailView(selectedReclamation);
                }
            });
            return row;
        });
        
        // Load initial data
        onRefresh();
    }

    private void filterReclamations() {
        if (allReclamations == null) return;

        String searchText = searchField.getText().toLowerCase();
        LocalDate selectedDate = datePicker.getValue();

        List<Reclamation> filteredList = allReclamations.stream()
            .filter(reclamation -> {
                boolean matchesText = searchText.isEmpty() ||
                    reclamation.getObjet().toLowerCase().contains(searchText) ||
                    reclamation.getCommentaire().toLowerCase().contains(searchText);
                
                boolean matchesDate = selectedDate == null ||
                    reclamation.getDate().equals(selectedDate.format(java.time.format.DateTimeFormatter.ISO_DATE));
                
                return matchesText && matchesDate;
            })
            .collect(Collectors.toList());

        tableViewReclamations.getItems().clear();
        tableViewReclamations.getItems().addAll(filteredList);
    }

    public void onRefresh() {
        allReclamations = service.afficher();
        filterReclamations();
    }

    public void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReclamation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEdit() {
        // Get the selected reclamation from the table
        Reclamation selectedReclamation = tableViewReclamations.getSelectionModel().getSelectedItem();

        if (selectedReclamation != null) {
            try {
                // Load the edit FXML form
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditReclamation.fxml"));
                Parent root = loader.load();
                EditReclamationController controller = loader.getController();

                // Initialize the controller with the selected reclamation
                controller.initData(selectedReclamation);

                // Show the edit window
                Stage stage = new Stage();
                stage.setTitle("Modifier Réclamation");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If no reclamation is selected, show a warning
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une réclamation à modifier !");
            alert.show();
        }
    }

    @FXML
    private void onDelete(ActionEvent event) throws SQLException {
        Reclamation selected = tableViewReclamations.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réclamation à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la réclamation");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ReclamationService service = new ReclamationService();
            service.supprimer(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation supprimée avec succès !");
            onRefresh(); // make sure this reloads the updated data
        }
    }

    @FXML
    private void onRepondre() {
        Reclamation selectedReclamation = tableViewReclamations.getSelectionModel().getSelectedItem();
        
        if (selectedReclamation == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une réclamation à laquelle répondre !");
            alert.show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReponse.fxml"));
            Parent root = loader.load();
            AddReponseController controller = loader.getController();
            
            // Pass the selected reclamation's ID to the response form
            controller.setReclamationId(selectedReclamation.getId());
            
            Stage stage = new Stage();
            stage.setTitle("Répondre à la Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de réponse !");
            alert.show();
        }
    }

    private void openDetailView(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReclamationDetail.fxml"));
            Parent root = loader.load();
            ReclamationDetailController controller = loader.getController();
            
            // Initialize the controller with the selected reclamation
            controller.initData(reclamation);
            
            Stage stage = new Stage();
            stage.setTitle("Détails de la Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la vue détaillée !");
            alert.show();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
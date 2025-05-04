package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Reponse;
import services.ReponseService;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;

public class DisplayReponseController {

    @FXML
    private TableView<Reponse> tableViewReponses;

    @FXML
    private TableColumn<Reponse, Integer> colId;

    @FXML
    private TableColumn<Reponse, String> colObjet;

    @FXML
    private TableColumn<Reponse, String> colMessage;

    @FXML
    private TableColumn<Reponse, String> colDate;

    @FXML
    private TableColumn<Reponse, Integer> colReclamationId;

    @FXML
    private TextField searchField;

    @FXML
    private DatePicker datePicker;

    private final ReponseService service = new ReponseService();
    private List<Reponse> allReponses;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colObjet.setCellValueFactory(new PropertyValueFactory<>("objet"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colReclamationId.setCellValueFactory(new PropertyValueFactory<>("reclamationId"));
        
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
            filterReponses();
        });
        
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterReponses();
        });
        
        // Load initial data
        onRefresh();
    }

    private void filterReponses() {
        if (allReponses == null) return;

        String searchText = searchField.getText().toLowerCase();
        LocalDate selectedDate = datePicker.getValue();

        List<Reponse> filteredList = allReponses.stream()
            .filter(reponse -> {
                boolean matchesText = searchText.isEmpty() ||
                    reponse.getObjet().toLowerCase().contains(searchText) ||
                    reponse.getCommentaire().toLowerCase().contains(searchText);
                
                boolean matchesDate = selectedDate == null ||
                    reponse.getDate().equals(selectedDate.format(java.time.format.DateTimeFormatter.ISO_DATE));
                
                return matchesText && matchesDate;
            })
            .collect(Collectors.toList());

        tableViewReponses.getItems().clear();
        tableViewReponses.getItems().addAll(filteredList);
    }

    public void onRefresh() {
        allReponses = service.afficher();
        filterReponses();
    }

    public void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReponse.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une Réponse");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEdit() {
        // Get the selected reponse from the table
        Reponse selectedReponse = tableViewReponses.getSelectionModel().getSelectedItem();

        if (selectedReponse != null) {
            try {
                // Load the edit FXML form
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditReponse.fxml"));
                Parent root = loader.load();
                EditReponseController controller = loader.getController();

                // Initialize the controller with the selected reponse
                controller.initData(selectedReponse);

                // Show the edit window
                Stage stage = new Stage();
                stage.setTitle("Modifier Réponse");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // If no reponse is selected, show a warning
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une réponse à modifier !");
            alert.show();
        }
    }

    @FXML
    private void onDelete(ActionEvent event) throws SQLException {
        Reponse selected = tableViewReponses.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réponse à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la réponse");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette réponse ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ReponseService service = new ReponseService();
            service.supprimer(selected.getId());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réponse supprimée avec succès !");
            onRefresh(); // make sure this reloads the updated data
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

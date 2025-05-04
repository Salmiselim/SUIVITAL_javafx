package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Medicament;
import models.Ordonnance;
import services.OrdonnanceService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DisplayOrdController {

    @FXML private VBox ordonnanceContainer;
    @FXML private TextField searchField;
    @FXML private Button toggleOrderButton;

    private final OrdonnanceService ordonnanceService = new OrdonnanceService();
    private List<Ordonnance> allOrdonnances = new ArrayList<>();
    private boolean isAscending = true;  // start ascending

    @FXML
    public void initialize() {
        setupSearch();
        loadOrdonnances();
        updateToggleButtonText();
    }

    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty()
                    .addListener((obs, oldVal, newVal) -> filterAndDisplay(newVal));
        }
    }

    private void loadOrdonnances() {
        try {
            allOrdonnances = ordonnanceService.getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterAndDisplay(searchField.getText());
    }

    private void filterAndDisplay(String keyword) {
        ordonnanceContainer.getChildren().clear();

        // sort by date
        allOrdonnances.sort(isAscending
                ? Comparator.comparing(Ordonnance::getDatePrescription)
                : Comparator.comparing(Ordonnance::getDatePrescription).reversed()
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String lowerKey = (keyword == null ? "" : keyword.toLowerCase());

        for (Ordonnance ord : allOrdonnances) {
            boolean matchDesc = ord.getDescription().toLowerCase().contains(lowerKey);
            boolean matchMed = ord.getMedicaments().stream()
                    .anyMatch(m -> m.getName().toLowerCase().contains(lowerKey));
            if (matchDesc || matchMed) {
                ordonnanceContainer.getChildren()
                        .add(createCard(ord, sdf));
            }
        }
    }

    private VBox createCard(Ordonnance ord, SimpleDateFormat sdf) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-border-color: #e9ecef;
            -fx-border-radius: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);
            """);

        // Description
        Label desc = new Label("📝 " + ord.getDescription());
        desc.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        desc.setWrapText(true);

        // Date
        Label date = new Label("📅 " + sdf.format(ord.getDatePrescription()));
        date.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Medications
        Label meds = new Label("💊 " + (ord.getMedicaments().isEmpty() 
            ? "Aucun médicament" 
            : String.join(", ", ord.getMedicaments().stream()
                .map(Medicament::getName)
                .limit(3)
                .toList()) + (ord.getMedicaments().size() > 3 ? "..." : "")));
        meds.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        meds.setWrapText(true);

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button details = new Button("Détails");
        Button update = new Button("Modifier");
        Button delete = new Button("Supprimer");

        String buttonStyle = """
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-padding: 8 15;
            -fx-background-radius: 25;
            -fx-cursor: hand;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);
            """;

        details.setStyle(buttonStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
        update.setStyle(buttonStyle + "-fx-background-color: #f39c12; -fx-text-fill: white;");
        delete.setStyle(buttonStyle + "-fx-background-color: #e74c3c; -fx-text-fill: white;");

        details.setOnAction(e -> openDetailsDialog(ord));
        update.setOnAction(e -> openUpdateDialog(ord));
        delete.setOnAction(e -> confirmAndDelete(ord));

        actions.getChildren().addAll(details, update, delete);
        card.getChildren().addAll(desc, date, meds, actions);

        return card;
    }

    private void confirmAndDelete(Ordonnance ord) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer cette ordonnance ?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(b -> {
            if (b == ButtonType.OK) {
                try {
                    ordonnanceService.delete(ord.getId());
                    loadOrdonnances();
                } catch (SQLException ex) {
                    new Alert(Alert.AlertType.ERROR,
                            "DB error: " + ex.getMessage(), ButtonType.OK).showAndWait();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,
                            "Error: " + ex.getMessage(), ButtonType.OK).showAndWait();
                }
            }
        });
    }

    private void openDetailsDialog(Ordonnance ord) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsOrd.fxml"));
            Parent root = loader.load();
            
            DetailsOrdonnanceController controller = loader.getController();
            controller.setOrdonnance(ord);
            
            Stage stage = new Stage();
            stage.setTitle("📝 Détails de l'Ordonnance");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /** Update form */
    private void openUpdateDialog(Ordonnance ord) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditOrd.fxml"));
            Parent root = loader.load();

            UpdateOrdonnanceController ctrl = loader.getController();
            ctrl.setOrdonnance(ord);
            ctrl.setOnUpdate(res -> loadOrdonnances());

            // Optional styling if needed again here
            root.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #ffffff, #f9f9f9);
            -fx-padding: 25;
            -fx-background-radius: 18;
            -fx-border-color: #ccc;
            -fx-border-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 12, 0.2, 0, 4);
        """);

            Scene scene = new Scene(root);
            Stage st = new Stage();
            st.initModality(Modality.APPLICATION_MODAL);
            st.setTitle("🛠 Modifier l'Ordonnance");
            st.setScene(scene);
            st.setResizable(false);
            st.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement du formulaire.").showAndWait();
        }
    }


    /** Navigate to AddOrd.fxml */
    @FXML
    private void openAddOrd(ActionEvent evt) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddOrd.fxml"));
            Parent root = loader.load();

            AddOrdonnanceController controller = loader.getController();
            controller.setOnOrdonnanceAdded(this::loadOrdonnances);

            // Use the same window
            Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            stage.setTitle("Ajouter une Ordonnance");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Toggle sort order */
    @FXML
    private void handleToggleOrder() {
        isAscending = !isAscending;
        updateToggleButtonText();
        filterAndDisplay(searchField.getText());
    }

    private void updateToggleButtonText() {
        toggleOrderButton.setText(
                isAscending
                        ? "Sort by Date ↓"
                        : "Sort by Date ↑"
        );
    }

    @FXML
    private void openSymptomSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SymptomSelection.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Sélection des Symptômes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de l'écran de sélection des symptômes").showAndWait();
        }
    }
}

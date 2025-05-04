package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class BaseLayoutController {
    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        showReclamations(); // Show reclamations by default
    }

    @FXML
    public void showReclamations() {
        loadContent("/DisplayReclamation.fxml");
    }

    @FXML
    public void showReponses() {
        loadContent("/DisplayReponse.fxml");
    }

    @FXML
    public void showStats() {
        loadContent("/Stats.fxml");
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 
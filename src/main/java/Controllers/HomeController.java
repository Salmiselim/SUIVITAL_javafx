package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import services.OrdonnanceService;
import services.MedService;
import java.io.IOException;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class HomeController {

    @FXML private Label ordonnanceCountLabel;
    @FXML private Label medicamentCountLabel;
    @FXML private LineChart<String, Number> ordonnanceChart;

    @FXML
    public void initialize() {
        try {
            OrdonnanceService ordonnanceService = new OrdonnanceService();
            MedService medService = new MedService();
            int ordonnanceCount = ordonnanceService.getAll().size();
            int medicamentCount = medService.getAll().size();
            ordonnanceCountLabel.setText(String.valueOf(ordonnanceCount));
            medicamentCountLabel.setText(String.valueOf(medicamentCount));

            // Fill the ordonnanceChart
            Map<String, Integer> dayCounts = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (models.Ordonnance ord : ordonnanceService.getAll()) {
                String day = sdf.format(ord.getDatePrescription());
                dayCounts.put(day, dayCounts.getOrDefault(day, 0) + 1);
            }
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ordonnances");
            List<String> sortedDays = new ArrayList<>(dayCounts.keySet());
            Collections.sort(sortedDays);
            for (String day : sortedDays) {
                series.getData().add(new XYChart.Data<>(day, dayCounts.get(day)));
            }
            ordonnanceChart.getData().clear();
            ordonnanceChart.getData().add(series);
        } catch (Exception e) {
            ordonnanceCountLabel.setText("-");
            medicamentCountLabel.setText("-");
        }
    }

    @FXML
    private void handleOrdonnanceButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/DisplayOrd.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Ordonnance");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleMedicamentButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/DisplayMed.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Ajouter un Médicament");
        stage.setScene(new Scene(root));
        stage.show();
    }
}

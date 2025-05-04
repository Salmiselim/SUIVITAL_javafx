package Controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import models.Reclamation;
import services.ReclamationService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsController {
    @FXML
    private LineChart<String, Number> monthlyChart;

    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        loadMonthlyData();
    }

    private void loadMonthlyData() {
        List<Reclamation> reclamations = reclamationService.afficher();
        
        // Group reclamations by month
        Map<String, Long> monthlyCounts = reclamations.stream()
            .collect(Collectors.groupingBy(
                reclamation -> {
                    LocalDate date = reclamation.getDate();
                    return date.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                },
                Collectors.counting()
            ));

        // Create data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de Réclamations");

        // Add data points
        monthlyCounts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        // Add series to chart
        monthlyChart.getData().add(series);
        
        // Style the chart
        monthlyChart.setCreateSymbols(true);
        monthlyChart.setLegendVisible(true);
    }
} 
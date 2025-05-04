package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Reclamation;
import models.Reponse;
import services.ReponseService;

import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.stage.FileChooser;

public class ReclamationDetailController {
    @FXML
    private Label lblObjet;
    @FXML
    private TextArea taMessage;
    @FXML
    private Label lblDate;
    @FXML
    private VBox responseContainer;
    @FXML
    private Label lblNoResponse;
    @FXML
    private VBox responseContent;
    @FXML
    private Label lblResponseObjet;
    @FXML
    private TextArea taResponseMessage;
    @FXML
    private Label lblResponseDate;

    private Reclamation reclamation;
    private final ReponseService reponseService = new ReponseService();

    public void initData(Reclamation reclamation) {
        this.reclamation = reclamation;
        displayReclamation();
        displayResponse();
    }

    private void displayReclamation() {
        lblObjet.setText(reclamation.getObjet());
        taMessage.setText(reclamation.getCommentaire());
        lblDate.setText(reclamation.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void displayResponse() {
        // Find response for this reclamation
        Reponse response = reponseService.afficher().stream()
                .filter(r -> r.getReclamationId() == reclamation.getId())
                .findFirst()
                .orElse(null);

        if (response != null) {
            // Show response content
            lblNoResponse.setVisible(false);
            responseContent.setVisible(true);
            
            lblResponseObjet.setText(response.getObjet());
            taResponseMessage.setText(response.getCommentaire());
            lblResponseDate.setText(response.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            // Show "no response" message
            lblNoResponse.setVisible(true);
            responseContent.setVisible(false);
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) lblObjet.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onExportPDF() {
        try {
            // Create a file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            // Set default filename
            String defaultFilename = "Reclamation_" + reclamation.getId() + ".pdf";
            fileChooser.setInitialFileName(defaultFilename);
            
            // Show save dialog
            File file = fileChooser.showSaveDialog(lblObjet.getScene().getWindow());
            
            if (file != null) {
                // Create PDF document
                PdfDocument pdf = new PdfDocument(new PdfWriter(file));
                Document document = new Document(pdf);
                
                // Add title
                Paragraph title = new Paragraph("Détails de la Réclamation")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold();
                document.add(title);
                
                // Add reclamation section
                document.add(new Paragraph("\nRéclamation").setBold());
                document.add(new Paragraph("Objet: " + reclamation.getObjet()));
                document.add(new Paragraph("Message: " + reclamation.getCommentaire()));
                document.add(new Paragraph("Date: " + reclamation.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                
                // Add response section if exists
                Reponse response = reponseService.afficher().stream()
                    .filter(r -> r.getReclamationId() == reclamation.getId())
                    .findFirst()
                    .orElse(null);
                
                if (response != null) {
                    document.add(new Paragraph("\nRéponse").setBold());
                    document.add(new Paragraph("Objet: " + response.getObjet()));
                    document.add(new Paragraph("Message: " + response.getCommentaire()));
                    document.add(new Paragraph("Date: " + response.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                }
                
                // Close document
                document.close();
                
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export PDF");
                alert.setHeaderText(null);
                alert.setContentText("Le PDF a été généré avec succès !");
                alert.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue lors de la génération du PDF.");
            alert.show();
        }
    }
} 
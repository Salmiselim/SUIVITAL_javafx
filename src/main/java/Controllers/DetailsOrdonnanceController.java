package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Separator;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import models.Medicament;
import models.Ordonnance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import services.QRCodeService;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;

public class DetailsOrdonnanceController {

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private VBox medicamentList;
    
    @FXML
    private HBox qrCodeContainer;

    private Ordonnance ordonnance;
    private final QRCodeService qrCodeService = new QRCodeService();

    public void setOrdonnance(Ordonnance ord) {
        this.ordonnance = ord;
        updateView();
    }

    private void updateView() {
        if (ordonnance == null) return;

        descriptionLabel.setText("📝 " + ordonnance.getDescription());
        dateLabel.setText("📅 Date: " + ordonnance.getDatePrescription());

        medicamentList.getChildren().clear();
        for (Medicament med : ordonnance.getMedicaments()) {
            VBox medBox = new VBox();
            medBox.setSpacing(3);
            medBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8px; -fx-border-color: #ccc;");

            Label name = new Label("💊 " + med.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label dosage = new Label("Dosage: " + med.getDosage());
            Label frequency = new Label("Frequency: " + med.getFrequency());
            Label duration = new Label("Duration: " + med.getDuration() + " days");

            medBox.getChildren().addAll(name, dosage, frequency, duration, new Separator());
            medicamentList.getChildren().add(medBox);
        }
        
        // Generate and display QR code
        ImageView qrCode = qrCodeService.generateQRCode(ordonnance);
        if (qrCode != null) {
            qrCodeContainer.getChildren().clear();
            qrCodeContainer.getChildren().add(qrCode);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) descriptionLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleExportPDF() {
        if (ordonnance == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer l'ordonnance");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Ordonnance_" + ordonnance.getDatePrescription() + ".pdf");
        
        File file = fileChooser.showSaveDialog(descriptionLabel.getScene().getWindow());
        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Header with logo and title
                Paragraph header = new Paragraph("SUIVITAL")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(24)
                        .setBold()
                        .setMarginBottom(20);
                document.add(header);

                // Subtitle
                document.add(new Paragraph("Ordonnance Médicale")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(18)
                        .setBold()
                        .setMarginBottom(30));

                // Doctor's information section
                Paragraph doctorInfo = new Paragraph("Médecin Prescripteur")
                        .setFontSize(14)
                        .setBold()
                        .setMarginBottom(10);
                document.add(doctorInfo);

                // Patient information section
                Paragraph patientInfo = new Paragraph("Informations du Patient")
                        .setFontSize(14)
                        .setBold()
                        .setMarginTop(20)
                        .setMarginBottom(10);
                document.add(patientInfo);

                // Description section
                document.add(new Paragraph("Description de l'ordonnance:")
                        .setFontSize(14)
                        .setBold()
                        .setMarginTop(20));
                document.add(new Paragraph(ordonnance.getDescription())
                        .setMarginBottom(20));

                // Date section
                document.add(new Paragraph("Date de prescription: " + ordonnance.getDatePrescription())
                        .setFontSize(12)
                        .setMarginBottom(30));

                // Medications table with improved styling
                Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
                table.setMarginTop(20);
                table.setMarginBottom(30);

                // Table header
                table.addHeaderCell(new Paragraph("Médicament").setBold());
                table.addHeaderCell(new Paragraph("Dosage").setBold());
                table.addHeaderCell(new Paragraph("Fréquence").setBold());
                table.addHeaderCell(new Paragraph("Durée").setBold());

                // Table content
                for (Medicament med : ordonnance.getMedicaments()) {
                    table.addCell(med.getName());
                    table.addCell(med.getDosage());
                    table.addCell(med.getFrequency());
                    table.addCell(med.getDuration() + " jours");
                }

                document.add(table);

                // Footer
                Paragraph footer = new Paragraph("© SUIVITAL - " + new java.util.Date().toString())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(10)
                        .setMarginTop(30);
                document.add(footer);

                document.close();
            } catch (IOException e) {
                e.printStackTrace();
                // You might want to show an error dialog here
            }
        }
    }
}

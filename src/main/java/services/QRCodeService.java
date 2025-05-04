package services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Ordonnance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QRCodeService {
    
    public ImageView generateQRCode(Ordonnance ordonnance) {
        try {
            // Create QR code data
            String qrData = String.format(
                "Ordonnance ID: %d\nDescription: %s\nDate: %s\nMédicaments: %s",
                ordonnance.getId(),
                ordonnance.getDescription(),
                ordonnance.getDatePrescription(),
                ordonnance.getMedicaments().stream()
                    .map(m -> m.getName())
                    .collect(java.util.stream.Collectors.joining(", "))
            );

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200);

            // Convert to JavaFX Image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageData = outputStream.toByteArray();
            Image image = new Image(new ByteArrayInputStream(imageData));

            // Create and return ImageView
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 0);");
            
            return imageView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 
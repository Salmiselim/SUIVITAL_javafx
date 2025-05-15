package outils;

import models.Post;
import models.Comment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PdfExporter {
    public static void exportPostToPdf(Post post, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Configuration des polices
                PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font contentFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                // Titre du post
                contentStream.setFont(titleFont, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(post.getTitle());
                contentStream.endText();

                // Métadonnées
                contentStream.setFont(contentFont, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("Published by:  " + post.getAuthor());
                contentStream.endText();

                // Contenu principal
                contentStream.setFont(contentFont, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 680);
                String[] contentLines = post.getContent().split("\n");
                for (String line : contentLines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -20);
                }
                contentStream.endText();

                // Section commentaires
                if (post.getComments() != null && !post.getComments().isEmpty()) {
                    contentStream.setFont(titleFont, 16);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 620);
                    contentStream.showText("Commentaires (" + post.getComments().size() + ")");
                    contentStream.endText();

                    contentStream.setFont(contentFont, 12);
                    int yPosition = 600;
                    for (Comment comment : post.getComments()) {
                        // En-tête commentaire
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, yPosition);
                        contentStream.showText(comment.getAuthor() + " - " +
                                comment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")));
                        contentStream.endText();

                        // Contenu commentaire
                        contentStream.beginText();
                        contentStream.newLineAtOffset(55, yPosition - 20);
                        String[] commentLines = comment.getContent().split("\n");
                        for (String line : commentLines) {
                            contentStream.showText(line);
                            contentStream.newLineAtOffset(0, -15);
                        }
                        contentStream.endText();

                        yPosition -= (30 + (commentLines.length * 15));
                    }
                }
            }
            document.save(new File(filePath));
        }
    }
}
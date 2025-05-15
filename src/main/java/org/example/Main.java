package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charge le fichier FXML principal (remplace par ton fichier de vue forum)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForumList.fxml"));
        Parent root = loader.load();

        // Configure la scène avec une taille adaptée à un forum
        Scene scene = new Scene(root, 1000, 700); // Largeur x Hauteur


        // scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Configure la fenêtre principale
        primaryStage.setTitle("Forum Suivital");
        primaryStage.setScene(scene);

        // Empeche le redimensionnement si tu veux une taille fixe
        primaryStage.setResizable(true);

        primaryStage.show();
    }
}
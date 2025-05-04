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
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BaseLayout.fxml"));
        Parent root = loader.load();

        // Set up the scene
        Scene scene = new Scene(root, 1200, 600);

        // Add the CSS to the scene
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Set up the stage
        primaryStage.setTitle("Gestion des Réclamations");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
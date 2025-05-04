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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
        Parent root = loader.load();

        // Create a large scene (Full HD)
        Scene scene = new Scene(root, 1400, 700);



        // Set up the stage
        primaryStage.setTitle("SuiviTale — Accueil");
        primaryStage.setScene(scene);
        // Optional: start in fullscreen or maximized
        primaryStage.setMaximized(true);

        primaryStage.show();
    }
}

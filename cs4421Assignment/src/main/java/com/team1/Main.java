package com.team1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Make sure the path to GUI.fxml is correct
            Parent root = FXMLLoader.load(getClass().getResource("/GUI.fxml"));
            Scene gui = new Scene(root);
            stage.setScene(gui);
            stage.setTitle("JavaFX Application"); // Optional title for the window
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Launches the JavaFX application
    }
}

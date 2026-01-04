package com.example.chapter4_challenge_diarymanager_gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        URL fxmlLocation = getClass().getResource("/com/example/chapter4_challenge_diarymanager_gui/MainDashboard.fxml");

        if (fxmlLocation == null) {
            throw new RuntimeException("Error: FXML file not found! Please check the folder name in resources.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);


        URL cssLocation = getClass().getResource("/com/example/chapter4_challenge_diarymanager_gui/styles.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        }

        stage.setTitle("Diary Manager");
        stage.setScene(scene);
        
        // Prevent the window from being resized
        stage.setResizable(false);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
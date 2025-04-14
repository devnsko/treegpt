package com.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Application");
        primaryStage.setFullScreen(true);

        primaryStage.setOnCloseRequest((event) -> {
            System.out.println("Closing Stage");
        });

        primaryStage.setOnHiding((event) -> {
            System.out.println("Hiding Stage");
        });

        primaryStage.setOnHidden((event) -> {
            System.out.println("Stage hidden");
        });


        primaryStage.setOnShowing((event) -> {
            System.out.println("Showing Stage");
        });


        primaryStage.setOnShown((event) -> {
            System.out.println("Stage Shown");
        });

        primaryStage.show();

        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();

        System.out.println("Application has started successfully.");

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
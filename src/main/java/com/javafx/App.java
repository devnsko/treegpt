package com.javafx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        // scene = new Scene(loadFXML("primary"), 640, 480);
        
        // Grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        // Textfield
        final TextField inp = new TextField();
        inp.setPromptText("Let's explore new!");
        inp.setPrefColumnCount(10);
        inp.getText();

        GridPane.setConstraints(inp, 0, 0);
        grid.getChildren().add(inp);

        Button sendBtn = new Button("Send");
        GridPane.setConstraints(sendBtn, 1, 0);
        grid.getChildren().add(sendBtn);

        Button clearBtn = new Button("Clear");
        GridPane.setConstraints(clearBtn, 2, 0);
        grid.getChildren().add(clearBtn);


        final Label label = new Label();
        GridPane.setConstraints(label, 0, 3);
        GridPane.setColumnSpan(label, 2);
        grid.getChildren().add(label);

        // Setting up the SUBMIT button
        sendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if((inp.getText() != null && !inp.getText().isEmpty())) {
                    label.setText(inp.getText() + "... So it is...");
                } else {
                    label.setText("u have empty msg");
                }
            }
        });

        // Setting up CLEAR buton
        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                inp.clear();
                label.setText(null);
            }
        });

        // Label lbl = new Label("Message: ");
        // TextField input = new TextField();
        // HBox hBox = new HBox();
        // hBox.getChildren().addAll(lbl, input);
        // hBox.setSpacing(10);
        
        scene = new Scene(grid, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Application");

        primaryStage.show();

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
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Properties props = new Properties();
    static {
        try {
            props.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String API_KEY = props.getProperty("OPENAI_API_KEY");
    
    OpenAIClient client = OpenAIOkHttpClient.builder()
    // Configures using the `OPENAI_API_KEY`, `OPENAI_ORG_ID`, `OPENAI_PROJECT_ID` and `OPENAI_BASE_URL` environment variables
    .fromEnv()
    .apiKey(API_KEY)
    .build();
    private static Scene scene;

    private static VBox chatBox = new VBox(5);
    private static TextField inp = new TextField();
    private static Button sendBtn = new Button("Send");
    private static Button clearBtn = new Button("Clear");
    private static Label issueLabel = new Label();

    @Override
    public void start(Stage primaryStage) throws IOException {
        // scene = new Scene(loadFXML("primary"), 640, 480);
        
        // Grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        // SetupTextfield
        inp.setPromptText("Let's explore new!");
        inp.setPrefColumnCount(10);
        inp.getText();

        GridPane.setConstraints(inp, 0, 0);
        grid.getChildren().add(inp);

        // Setup Send button
        GridPane.setConstraints(sendBtn, 1, 0);
        grid.getChildren().add(sendBtn);

        // Setup Clear button
        GridPane.setConstraints(clearBtn, 2, 0);
        grid.getChildren().add(clearBtn);

        // Setup Action Label
        GridPane.setConstraints(issueLabel, 0, 3);
        GridPane.setColumnSpan(issueLabel, 2);
        grid.getChildren().add(issueLabel);

        GridPane.setConstraints(chatBox, 0, 3);
        grid.getChildren().add(chatBox);

        // Setting up the SUBMIT action
        sendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if((inp.getText() != null && !inp.getText().isEmpty())) {
                    createMessageBlock(inp.getText());
                    talkGPT(inp.getText());
                    clearFields();
                    updateUI();
                } else {
                    issueLabel.setText("u have empty msg");
                }
            }
        });

        // Setting up CLEAR action
        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                clearFields();
                updateUI();
            }
        });

        // Setting up listening to the text field
        inp.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                updateUI();
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
        updateUI();
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

    public void clearFields() {
        inp.clear();
        issueLabel.setText(null);
    }

    public void updateUI() {
        boolean isEmpty = inp.getText() == "";
        sendBtn.setDisable(isEmpty);
        clearBtn.setDisable(isEmpty);
    }

    public boolean createMessageBlock(String msg) {
        try {
            Text t = new Text(msg);
            HBox messageBlock = new HBox();
            messageBlock.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
            messageBlock.getChildren().add(t);
            chatBox.getChildren().add(messageBlock);
            return true;
        } catch (Exception e) {
            System.out.println("Error while trying to create message box: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void talkGPT(String message) {
        Response answer = messageGPT(message);        
        // System.out.println(answer.toString());
        String text = answer.output().get(0).asMessage().content().get(0).asOutputText().text();
        if(!createMessageBlock(text)) {
            System.out.println("Some issues with gpt response");
        }
    }

    public Response messageGPT(String message) {
        ResponseCreateParams params = ResponseCreateParams.builder()
        .input("short answer: " + message)
        .model(ChatModel.GPT_4_1)
        .build();
        Response response = client.responses().create(params);
        return response;
    }

}
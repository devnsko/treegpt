package com.javafx;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.geometry.Insets;

import com.google.gson.*;
import java.net.http.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.*;

public class ChatTreeApp extends Application {
    private TreeItem<String> rootNode;
    private TreeView<String> treeView;
    private TextField inputField;
    private Button sendButton;
    private HttpClient httpClient;
    private static Properties props = new Properties();
    static {
        try {
            props.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String openaiApiKey =props.getProperty("OPENAI_API_KEY"); // Set your API key as env variable
    private String openaiEndpoint = "https://api.openai.com/v1/chat/completions";
    // Map each tree node to its conversation history
    private Map<TreeItem<String>, List<Map<String, String>>> conversationMap = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        httpClient = HttpClient.newHttpClient();
        rootNode = new TreeItem<>("Chat");
        rootNode.setExpanded(true);
        treeView = new TreeView<>(rootNode);
        conversationMap.put(rootNode, new ArrayList<>());

        inputField = new TextField();
        inputField.setPromptText("Type your message...");
        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        HBox inputBox = new HBox(5, inputField, sendButton);
        inputBox.setPadding(new Insets(10));

        VBox layout = new VBox(5, treeView, inputBox);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX OpenAI Chat Tree");
        primaryStage.show();
    }

    private void sendMessage() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty()) return;
        inputField.clear();

        // Determine parent node (selected or root)
        TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
        TreeItem<String> parentNode = (selected != null) ? selected : rootNode;

        // Clone parent's history and append user message
        List<Map<String, String>> history = new ArrayList<>(conversationMap.getOrDefault(parentNode, new ArrayList<>()));
        history.add(Map.of("role", "user", "content", userText));

        // Add user node to tree
        TreeItem<String> userNode = new TreeItem<>("User: " + userText);
        parentNode.getChildren().add(userNode);
        parentNode.setExpanded(true);
        conversationMap.put(userNode, history);

        // Call OpenAI asynchronously
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return callOpenAI(history);
            }
        };

        task.setOnSucceeded(evt -> {
            String response = task.getValue();
            // Append assistant message
            List<Map<String, String>> newHistory = new ArrayList<>(history);
            newHistory.add(Map.of("role", "assistant", "content", response));

            TreeItem<String> aiNode = new TreeItem<>("AI: " + response);
            userNode.getChildren().add(aiNode);
            userNode.setExpanded(true);
            conversationMap.put(aiNode, newHistory);
        });

        task.setOnFailed(evt -> {
            String error = task.getException().getMessage();
            TreeItem<String> errorNode = new TreeItem<>("Error: " + error);
            userNode.getChildren().add(errorNode);
            userNode.setExpanded(true);
        });

        new Thread(task).start();
    }

    private String callOpenAI(List<Map<String, String>> messages) throws Exception {
        Gson gson = new Gson();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", messages);
        String body = gson.toJson(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(openaiEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openaiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("API error: " + response.body());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        JsonArray choices = json.getAsJsonArray("choices");
        JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
        return message.get("content").getAsString();
    }
}

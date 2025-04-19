package com.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import com.google.gson.*;
import java.net.http.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class ChatTreeApp2 extends Application {
    private TreeItem<String> rootNode;
    private TreeView<String> treeView;
    private TextField inputField;
    private Button sendButton;
    private HttpClient httpClient;
    private static Properties props = new Properties();
    static {
        try { props.load(new FileInputStream("src/main/resources/application.properties")); }
        catch (IOException e) { e.printStackTrace(); }
    }
    private static String openaiApiKey = props.getProperty("OPENAI_API_KEY");
    private String openaiEndpoint = "https://api.openai.com/v1/chat/completions";
    private Map<TreeItem<String>, List<Map<String, String>>> conversationMap = new HashMap<>();

    private TextArea nodeContentArea;
    private TextField commentField;
    private Button expandButton;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        httpClient = HttpClient.newHttpClient();
        rootNode = new TreeItem<>("Chat"); rootNode.setExpanded(true);
        treeView = new TreeView<>(rootNode);
        conversationMap.put(rootNode, new ArrayList<>());
        configureContextMenu();

        // Selection listener
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                String val = newSel.getValue();
                int idx = val.indexOf(": ");
                String content = (idx >= 0) ? val.substring(idx + 2) : val;
                nodeContentArea.setText(content);
            }
        });

        // Input area
        inputField = new TextField(); inputField.setPromptText("Type your message...");
        inputField.setOnKeyPressed(event -> {
            if (new KeyCodeCombination(KeyCode.ENTER).match(event)) sendMessage();
        });
        sendButton = new Button("Send"); sendButton.setOnAction(e -> sendMessage());
        HBox inputBox = new HBox(5, inputField, sendButton);
        inputBox.setPadding(new Insets(10));

        // Expansion area
        nodeContentArea = new TextArea(); nodeContentArea.setEditable(false); nodeContentArea.setWrapText(true);
        nodeContentArea.setPromptText("Select AI response to expand");
        commentField = new TextField(); commentField.setPromptText("Comment (optional)");
        expandButton = new Button("Expand"); expandButton.setOnAction(e -> expandSelection());
        MenuItem expandShortcut = new MenuItem(); expandShortcut.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        expandButton.getScene(); // ensure scene exists
        HBox expandBox = new HBox(5, expandButton, commentField);
        expandBox.setPadding(new Insets(10));

        VBox rightPane = new VBox(5,
            new Label("Selected Content:"), nodeContentArea,
            new Label("Detail & comment:"), expandBox
        );
        rightPane.setPadding(new Insets(10));

        SplitPane splitPane = new SplitPane(treeView, rightPane);
        splitPane.setDividerPositions(0.3);

        // Menu bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit"); exitItem.setOnAction(e -> primaryStage.close());
        fileMenu.getItems().add(exitItem);
        Menu editMenu = new Menu("Edit");
        MenuItem clearTree = new MenuItem("Clear Tree"); clearTree.setOnAction(e -> {
            rootNode.getChildren().clear(); conversationMap.clear(); conversationMap.put(rootNode, new ArrayList<>());
        });
        editMenu.getItems().add(clearTree);
        Menu settingsMenu = new Menu("Settings");
        MenuItem configureModel = new MenuItem("Configure API Key / Model");
        configureModel.setOnAction(e -> showSettingsDialog());
        settingsMenu.getItems().add(configureModel);
        menuBar.getMenus().addAll(fileMenu, editMenu, settingsMenu);

        VBox mainLayout = new VBox(menuBar, splitPane, inputBox);
        Scene scene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX OpenAI Chat Tree");
        primaryStage.show();
    }

    private void configureContextMenu() {
        ContextMenu ctx = new ContextMenu();
        MenuItem replyItem = new MenuItem("Reply Here"); replyItem.setOnAction(e -> inputField.requestFocus());
        MenuItem expandItem = new MenuItem("Expand Selection"); expandItem.setOnAction(e -> expandSelection());
        MenuItem copyItem = new MenuItem("Copy Text"); copyItem.setOnAction(e -> {
            String text = nodeContentArea.getSelectedText().isEmpty() ? nodeContentArea.getText() : nodeContentArea.getSelectedText();
            Clipboard.getSystemClipboard().setContent(Map.of(DataFormat.PLAIN_TEXT, text));
        });
        MenuItem deleteItem = new MenuItem("Delete Node"); deleteItem.setOnAction(e -> {
            TreeItem<String> sel = treeView.getSelectionModel().getSelectedItem();
            if (sel != null && sel != rootNode) sel.getParent().getChildren().remove(sel);
        });
        ctx.getItems().addAll(replyItem, expandItem, copyItem, deleteItem);
        treeView.setContextMenu(ctx);
    }

    private void sendMessage() {
        String text = inputField.getText().trim(); if (text.isEmpty()) return;
        inputField.clear();
        TreeItem<String> sel = treeView.getSelectionModel().getSelectedItem();
        TreeItem<String> parent = sel != null ? sel : rootNode;
        List<Map<String, String>> hist = new ArrayList<>(conversationMap.getOrDefault(parent, new ArrayList<>()));
        hist.add(Map.of("role","user","content",text));
        TreeItem<String> userNode = new TreeItem<>("User: "+text);
        parent.getChildren().add(userNode); parent.setExpanded(true);
        conversationMap.put(userNode, hist);
        callOpenAIAsync(hist, userNode);
    }

    private void expandSelection() {
        TreeItem<String> sel = treeView.getSelectionModel().getSelectedItem();
        if (sel==null || !sel.getValue().startsWith("AI: ")) { showAlert("Select AI node"); return; }
        String selText = nodeContentArea.getSelectedText().trim();
        if (selText.isEmpty()) { showAlert("Select text to expand"); return; }
        String comment = commentField.getText().trim();
        List<Map<String,String>> hist = new ArrayList<>(conversationMap.getOrDefault(sel, new ArrayList<>()));
        String prompt = "Elaborate: \""+selText+"\""+(comment.isEmpty()?"":". Comment: \""+comment+"\"");
        hist.add(Map.of("role","user","content",prompt));
        TreeItem<String> expNode = new TreeItem<>("Expand: "+selText);
        sel.getChildren().add(expNode); sel.setExpanded(true);
        conversationMap.put(expNode,hist); commentField.clear();
        callOpenAIAsync(hist, expNode);
    }

    private void callOpenAIAsync(List<Map<String, String>> history, TreeItem<String> parentNode) {
        Task<String> task = new Task<>(){ protected String call() throws Exception { return callOpenAI(history);} };
        task.setOnSucceeded(evt -> {
            String resp = task.getValue();
            List<Map<String, String>> newHist = new ArrayList<>(history);
            newHist.add(Map.of("role","assistant","content",resp));
            TreeItem<String> aiNode = new TreeItem<>("AI: "+resp);
            parentNode.getChildren().add(aiNode); parentNode.setExpanded(true);
            conversationMap.put(aiNode,newHist);
        });
        task.setOnFailed(evt -> {
            TreeItem<String> err = new TreeItem<>("Error: "+task.getException().getMessage());
            parentNode.getChildren().add(err); parentNode.setExpanded(true);
        });
        new Thread(task).start();
    }

    private String callOpenAI(List<Map<String, String>> messages) throws Exception {
        Gson gson = new Gson(); Map<String,Object> req = new HashMap<>();
        req.put("model","gpt-3.5-turbo"); req.put("messages",messages);
        String body = gson.toJson(req);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(openaiEndpoint))
            .header("Content-Type","application/json")
            .header("Authorization","Bearer "+openaiApiKey)
            .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> resp = httpClient.send(request,HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode()!=200) throw new RuntimeException("API error: "+resp.body());
        JsonObject json = gson.fromJson(resp.body(),JsonObject.class);
        return json.getAsJsonArray("choices").get(0).getAsJsonObject()
                   .getAsJsonObject("message").get("content").getAsString();
    }

    private void showSettingsDialog() {
        TextInputDialog dialog = new TextInputDialog(openaiApiKey);
        dialog.setTitle("Settings"); dialog.setHeaderText("Configure OpenAI API Key");
        dialog.setContentText("API Key:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(key -> openaiApiKey = key);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
    }
}

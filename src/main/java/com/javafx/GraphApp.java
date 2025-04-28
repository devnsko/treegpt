package com.javafx;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
// import javafx.animation.AnimationTimer;
import javafx.scene.input.ScrollEvent;

import java.util.*;

public class GraphApp extends Application {

    private final Group root = new Group();
    private final Group graphGroup = new Group();
    private PerspectiveCamera camera;
    private double anchorX, anchorY;
    private double angleX = 0, angleY = 0;
    private double zoom = -500;

    @Override
    public void start(Stage stage) {
        root.getChildren().add(graphGroup);

        // Загрузка данных
        String latestFile = GraphLoader.getLatestGraphFile(".treegpt/graphs");
        if (latestFile == null) {
            System.err.println("❌ Can't find any graphs");
            return;
        }
        List<GraphNode> nodes = GraphLoader.load(latestFile);
        System.out.println("📂 Loaded graph from: " + latestFile);


        // Добавление узлов
        for (GraphNode node : nodes) {
            graphGroup.getChildren().add(node.getSphere());
            System.err.println(graphGroup.computeAreaInScreen()); /////////////////
        }

        // Добавление связей
        for (GraphNode node : nodes) {
            if (node.parentId != null) {
                GraphNode parent = GraphNode.findById(nodes, node.parentId);
                if (parent != null) {
                    Cylinder line = GraphUtils.connect(node, parent);
                    graphGroup.getChildren().add(line);
                }
            }
            System.err.println(node.id); //////
        }
        // Базовый рассеянный свет
        AmbientLight ambientLight = new AmbientLight(Color.rgb(200, 200, 200));
        root.getChildren().add(ambientLight);

        // Точечный направленный свет
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-500);
        pointLight.setTranslateZ(-500);
        root.getChildren().add(pointLight);


        // Камера
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
            new Rotate(-20, Rotate.X_AXIS),
            new Rotate(-20, Rotate.Y_AXIS),
            new Translate(0, 0, zoom)
        );

        Scene scene = new Scene(root, 1200, 800, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.GRAY);
        scene.setCamera(camera);
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        Translate translate = new Translate(0, 0, 0);

        graphGroup.getTransforms().addAll(rotateX, rotateY, translate);

        
        // Вращение мышкой
        scene.setOnMousePressed(e -> {
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
        });
        
        scene.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - anchorX;
            double deltaY = e.getSceneY() - anchorY;
        
            if (e.isPrimaryButtonDown()) { // ЛКМ — вращение
                rotateY.setAngle(rotateY.getAngle() + deltaX / 2);
                rotateX.setAngle(rotateX.getAngle() - deltaY / 2);
            } else if (e.isSecondaryButtonDown() || e.isMiddleButtonDown()) { // ПКМ или СКМ — перемещение
                translate.setX(translate.getX() + deltaX);
                translate.setY(translate.getY() + deltaY);
            }
        
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
        });
        
        

        scene.addEventHandler(ScrollEvent.SCROLL, event -> {
            zoom += event.getDeltaY();
            camera.setTranslateZ(zoom);
        });

        stage.setTitle("3D GPT Message Graph");
        stage.setScene(scene);
        stage.show();

        // Print HTML representation of the stage
        StringBuilder html = new StringBuilder();
        html.append("<html><body><ul>");
        for (Node node : root.getChildren()) {
            html.append("<li>").append(node.getClass().getSimpleName()).append("</li>");
            if (node instanceof Parent) {
            html.append("<ul>");
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                html.append("<li>").append(child.getClass().getSimpleName()).append("</li>");
            }
            html.append("</ul>");
            }
        }
        html.append("</ul></body></html>");
        System.out.println(html.toString().replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>"));
    }

    public static void main(String[] args) {
        launch();
    }
}


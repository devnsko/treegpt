package com.javafx;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.animation.AnimationTimer;
import javafx.scene.input.ScrollEvent;

import java.util.*;

public class GraphApp extends Application {

    private final Group root = new Group();
    private final Group graphGroup = new Group();
    private PerspectiveCamera camera;
    private double anchorX, anchorY;
    private double angleX = 0, angleY = 0;
    private double zoom = -1000;

    @Override
    public void start(Stage stage) {
        root.getChildren().add(graphGroup);

        // Загрузка данных
        String latestFile = GraphLoader.getLatestGraphFile(".treegpt/graphs");
        if (latestFile == null) {
            System.err.println("❌ Не удалось найти ни одного графа!");
            return;
        }
        List<GraphNode> nodes = GraphLoader.load(latestFile);
        System.out.println("📂 Загружен граф из: " + latestFile);


        // Добавление узлов
        for (GraphNode node : nodes) {
            graphGroup.getChildren().add(node.getSphere());
        }

        // Добавление связей
        for (GraphNode node : nodes) {
            if (node.parentId != null) {
                GraphNode parent = GraphNode.findById(nodes, node.parentId);
                if (parent != null) {
                    Line line = GraphUtils.connect(node, parent);
                    graphGroup.getChildren().add(line);
                }
            }
        }

        // Камера
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
            new Rotate(-20, Rotate.X_AXIS),
            new Rotate(-20, Rotate.Y_AXIS),
            new Translate(0, 0, zoom)
        );

        Scene scene = new Scene(root, 1200, 800, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);

        // Вращение мышкой
        scene.setOnMousePressed(e -> {
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
        });

        scene.setOnMouseDragged(e -> {
            angleY += (e.getSceneX() - anchorX) / 10;
            angleX -= (e.getSceneY() - anchorY) / 10;
            graphGroup.setRotationAxis(Rotate.Y_AXIS);
            graphGroup.setRotate(angleY);
            graphGroup.setRotationAxis(Rotate.X_AXIS);
            graphGroup.setRotate(angleX);
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
    }

    public static void main(String[] args) {
        launch();
    }
}


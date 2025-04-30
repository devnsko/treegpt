package com.javafx;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.util.*;

public class GraphApp extends Application {

    // private double anchorX, anchorY;
    // private double angleX = 0, angleY = 0;
    // private double zoom = -500;
    
    
    final Group root = new Group();
    final Xform graphGroup = new Xform();
    final Xform world = new Xform();
    final Xform nodeGroup = new Xform();
    final Xform edgeGroup = new Xform();
    final Xform axisGroup = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    private static final double AXIS_LENGTH = 250.0;

    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 0.5;
    private static final double TRACK_SPEED = 0.3;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.WHITE);
        handleKeyboard(scene, world);
        handleMouse(scene, world);
        
        root.getChildren().add(world);
        buildCamera();
        buildAxes();
        buildGraph();

        AmbientLight ambientLight = new AmbientLight(Color.rgb(200, 200, 200));
        root.getChildren().add(ambientLight);

        
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-500);
        pointLight.setTranslateZ(-500);
        root.getChildren().add(pointLight);
        
        stage.setTitle("3D GPT Message Graph");
        stage.setScene(scene);
        stage.show();
        scene.setCamera(camera);

    }

    private void buildGraph() {
        String latestFile = GraphLoader.getLatestGraphFile(".treegpt/graphs");
        if (latestFile == null) {
            System.err.println("[FAIL] Can't find any graphs");
            return;
        }
        List<GraphNode> nodes = GraphLoader.load(latestFile);
        System.out.println("[SUCCESS] Loaded graph from: " + latestFile);


        for (GraphNode node : nodes) {
            nodeGroup.getChildren().add(node.getXform());
            System.err.println(nodeGroup.computeAreaInScreen()); 
        }

        for (GraphNode node : nodes) {
            if (node.parentId != null) {
                GraphNode parent = GraphNode.findById(nodes, node.parentId);
                if (parent != null) {
                    CylinderXform line = GraphUtils.connect(node.getXform(), parent.getXform());
                    edgeGroup.getChildren().add(line);
                    node.getXform().debug();
                    parent.getXform().debug();
                    line.debug();
                }
            }
            System.err.println(node.id); 
        }
        graphGroup.getChildren().addAll(nodeGroup, edgeGroup);
        root.getChildren().add(graphGroup);
    }

    
    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(false);
        world.getChildren().addAll(axisGroup);
    }

    
    private void handleMouse(Scene scene, final Node root) {

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);
                
                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() -
                    mouseDeltaX*modifier*ROTATION_SPEED);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() +
                    mouseDeltaY*modifier*ROTATION_SPEED);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() +
                    mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);
                    cameraXform2.t.setY(cameraXform2.t.getY() +
                    mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);
                }
            }
        });
        // TODO: Zoom In/Out
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent me) {
                double scroll = me.getDeltaY();
                if (me.isControlDown()) scroll *= SHIFT_MULTIPLIER;
                double z = camera.getTranslateZ();
                double newZ = z + scroll * MOUSE_SPEED;
                camera.setTranslateZ(newZ);
                
            }
        });
    }

    private void handleKeyboard(Scene scene, final Node root) {

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (! event.isControlDown()) return;
                switch (event.getCode()) {
                    case Z:
                        cameraXform2.t.setX(0.0);
                        cameraXform2.t.setY(0.0);
                        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                        break;
                    case X:
                        axisGroup.setVisible(!axisGroup.isVisible());
                        break;
                    case V:
                        edgeGroup.setVisible(!edgeGroup.isVisible());
                        break;
                    case B:
                        nodeGroup.setVisible(!nodeGroup.isVisible());
                        break;
                    case M:
                    // TODO: CHECK mixed EVERY angle rotation
                        mixingAngles();
                    default:
                        break;
                }
            }
        });
    }

    private void mixingAngles() {
        // edgeGroup.getChildren().forEach();
    }

    public static void main(String[] args) {
        launch();
    }
}


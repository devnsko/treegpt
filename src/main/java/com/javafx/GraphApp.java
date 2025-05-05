package com.javafx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;

import java.util.*;

public class GraphApp extends Application {
    
    final Group root = new Group();
    final Xform graphGroup = new Xform();
    final List<Node> allSpheres = new ArrayList<>();
    final Xform world = new Xform();
    final Xform nodeGroup = new Xform();
    final Xform edgeGroup = new Xform();
    final Xform axisGroup = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -150;
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
        scene.setFill(Color.rgb(25, 25, 25));
        handleKeyboard(scene, world);
        handleMouse(scene, world);
        
        root.getChildren().add(world);
        buildCamera();
        buildAxes();
        buildGraph();
        buildLight();
        stage.setTitle("3D GPT Message Graph");
        stage.setScene(scene);
        stage.show();
        scene.setCamera(camera);

    }

    private void buildLight() {
        AmbientLight ambientLight = new AmbientLight(Color.rgb(255, 255, 255));
        root.getChildren().add(ambientLight);
        
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-500);
        pointLight.setTranslateZ(-500);
        root.getChildren().add(pointLight);
        
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
            allSpheres.add(node.getSphere());
        }

        for (GraphNode node : nodes) {
            if (node.parentId != null) {
                GraphNode parent = GraphNode.findByReplyId(nodes, node.parentId);
                if (parent != null) {
                    CylinderXform line = GraphUtils.connect(node.getXform(), parent.getXform());
                    edgeGroup.getChildren().add(line);
                }
            }
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
                    case P:
                        // TODO: Fix Camera's center-pivot changing
                        List<Node> AllNode = new ArrayList<>();
                        AllNode.addAll(allSpheres);
                        AllNode.addAll(graphGroup.getChildren());
                        AllNode.addAll(getAllNodes(graphGroup));
                        PickResult result = manualRayPick(camera, AllNode, 100000.0);
                        if (result != null) {
                            Point3D point = result.getIntersectedPoint();
                            cameraXform3.setPivot(point.getX(), point.getY(), point.getZ());
                            System.out.printf("🎯 Focus set to (%.3f, %.3f, %.3f)%n", point.getX(), point.getY(), point.getZ());
                        }
                    default:
                        break;
                }
            }
        });
    }

    

    private List<Node> getAllNodes(Group root) {
        return getAllNodes(root, new ArrayList<>());
    }

    private List<Node> getAllNodes(Group root, List<Node> children) {
        for (Node node : root.getChildren()) {
            children.add(node);
            if (node instanceof Group) {
                getAllNodes((Group) node, children);
            }
        }
        return children;
    }

    private Point3D rayIntersectsAABB(Point3D origin, Point3D dir, Bounds box) {
        double tMin = (box.getMinX() - origin.getX()) / dir.getX();
        double tMax = (box.getMaxX() - origin.getX()) / dir.getX();
        if (tMin > tMax) { double tmp = tMin; tMin = tMax; tMax = tmp; }
    
        double tyMin = (box.getMinY() - origin.getY()) / dir.getY();
        double tyMax = (box.getMaxY() - origin.getY()) / dir.getY();
        if (tyMin > tyMax) { double tmp = tyMin; tyMin = tyMax; tyMax = tmp; }
    
        if ((tMin > tyMax) || (tyMin > tMax)) return null;
        if (tyMin > tMin) tMin = tyMin;
        if (tyMax < tMax) tMax = tyMax;
    
        double tzMin = (box.getMinZ() - origin.getZ()) / dir.getZ();
        double tzMax = (box.getMaxZ() - origin.getZ()) / dir.getZ();
        if (tzMin > tzMax) { double tmp = tzMin; tzMin = tzMax; tzMax = tmp; }
    
        if ((tMin > tzMax) || (tzMin > tMax)) return null;
        if (tzMin > tMin) tMin = tzMin;
        if (tzMax < tMax) tMax = tzMax;
    
        if (tMin < 0) return null; // луч позади
    
        return origin.add(dir.multiply(tMin)); // точка входа в AABB
    }
    
    private PickResult manualRayPick(Camera camera, List<Node> nodes, double maxDistance) {
        Transform transform = camera.getLocalToSceneTransform();
        Point3D camPos = transform.transform(Point3D.ZERO);
        Point3D dir = transform.deltaTransform(new Point3D(0, 0, -1)).normalize().multiply(-1);
    
        Node closestNode = null;
        Point3D closestPoint = null;
        double closestDist = maxDistance;
    
        for (Node node : nodes) {
            Bounds bounds = node.localToScene(node.getBoundsInLocal());
            Point3D hitPoint = rayIntersectsAABB(camPos, dir, bounds);
            if (hitPoint != null) {
                double dist = camPos.distance(hitPoint);
                if (dist < closestDist) {
                    closestDist = dist;
                    closestPoint = hitPoint;
                    closestNode = node;
                }
            }
        }
    
        if (closestPoint != null) {
            return new PickResult(closestNode, closestPoint, PickResult.FACE_UNDEFINED);
        }
    
        return null;
    }
    


    public static void main(String[] args) {
        launch();
    }
}


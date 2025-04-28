package com.javafx;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.util.Random;

public class GraphUtils {
    public static Color colorByCluster(int cluster) {
        Random rand = new Random(cluster * 99991);
        return Color.hsb(rand.nextInt(360), 0.9, 0.8);
    }

    public static Cylinder connect(GraphNode a, GraphNode b) {
         double startX = a.x * 50;
        double startY = a.y * 50;
        double startZ = a.z * 50;
        double endX = b.x * 50;
        double endY = b.y * 50;
        double endZ = b.z * 50;

        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;

        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);

        Cylinder line = new Cylinder(1, distance); 

        line.setTranslateX((startX + endX) / 2);
        line.setTranslateY((startY + endY) / 2);
        line.setTranslateZ((startZ + endZ) / 2);

        double phi = Math.atan2(dy, dx);
        double theta = Math.acos(dz / distance);

        line.getTransforms().addAll(
            new Rotate(-Math.toDegrees(phi), Rotate.Z_AXIS),
            new Rotate(Math.toDegrees(theta), Rotate.Y_AXIS)
        );

        line.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
        return line;
    }
}

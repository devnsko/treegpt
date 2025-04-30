package com.javafx;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;

import java.util.Random;

public class GraphUtils {
    public static Color colorByCluster(int cluster) {
        Random rand = new Random(cluster * 99991);
        return Color.hsb(rand.nextInt(360), 0.9, 0.8);
    }

    public static CylinderXform connect(Xform a, Xform b) {
        double startX = a.t.getX();
        double startY = a.t.getY();
        double startZ = a.t.getZ();
        double endX = b.t.getX();
        double endY = b.t.getY();
        double endZ = b.t.getZ();
        System.err.println(startX+startY+startZ+endX+endY+endZ);
        System.err.printf("Start: (%.2f, %.2f, %.2f) End: (%.2f, %.2f, %.2f)%n", startX, startY, startZ, endX, endY, endZ);


        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;

        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
        System.err.println("========"+distance);
        double azimuth = Math.toDegrees(Math.atan2(dy, dx));
        double elevation = Math.toDegrees(Math.atan2(dz, Math.sqrt(dx*dx + dy*dy)));

        // Rotate rotateY = new Rotate(azimuth, Rotate.Y_AXIS);
        // Rotate rotateX = new Rotate(-elevation, Rotate.X_AXIS);
        
        CylinderXform line = new CylinderXform(0.01, distance/90.0);

        line.setTranslate(startX, startY, startZ);
        line.cylinder.setTranslateX(distance/2.0);
        line.cylinder.setRotationAxis(Rotate.Z_AXIS);
        line.cylinder.setRotate(90.0);

        line.setRotateY(azimuth);
        line.setRotateX(-elevation);

        // line.getTransforms().addAll(rotateY, rotateX);

        line.cylinder.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
        return line;
    }
}

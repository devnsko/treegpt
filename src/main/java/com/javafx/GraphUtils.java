package com.javafx;

import javafx.geometry.Point3D;
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
        Point3D dotA = a.localToScene(Point3D.ZERO);
        Point3D dotB = b.localToScene(Point3D.ZERO);
        // System.err.printf("Start: (%.2f, %.2f, %.2f) End: (%.2f, %.2f, %.2f)%n", startX, startY, startZ, endX, endY, endZ);

        CylinderXform line = new CylinderXform(dotA, dotB);

        // line.setTranslate(startX, startY, startZ);
        // line.cylinder.setTranslateX(distance/2.0);
        // line.cylinder.setRotationAxis(Rotate.Z_AXIS);
        // line.cylinder.setRotate(90.0);

        // line.setRotateY(azimuth);
        // line.setRotateX(elevation);

        // // line.getTransforms().addAll(rotateY, rotateX);

        line.cylinder.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
        return line;
    }
}

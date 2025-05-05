package com.javafx;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

import java.util.Random;

public class GraphUtils {
    public static Color colorByCluster(int cluster) {
        Random rand = new Random(cluster * 99991);
        return Color.hsb(rand.nextInt(360), 0.9, 0.8);
    }

    public static CylinderXform connect(Xform a, Xform b) {
        Point3D dotA = a.localToScene(Point3D.ZERO);
        Point3D dotB = b.localToScene(Point3D.ZERO);
        CylinderXform line = new CylinderXform(dotA, dotB);

        PhongMaterial edgeMaterial = new PhongMaterial();
        edgeMaterial.setDiffuseColor(Color.GREY);
        edgeMaterial.setSpecularColor(Color.LIGHTBLUE);
        line.cylinder.setMaterial(edgeMaterial);
        return line;
    }
}

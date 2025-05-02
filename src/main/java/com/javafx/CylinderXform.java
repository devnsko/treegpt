package com.javafx;

import javafx.geometry.Point3D;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class CylinderXform extends Xform {
    public Cylinder cylinder;

    public CylinderXform() {
        super();
        cylinder = new Cylinder();
        this.getChildren().add(cylinder);
    }
    
    public CylinderXform(Point3D origin, Point3D target) {
        super();
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();
        cylinder = new Cylinder(0.0025, height);
        this.getChildren().add(cylinder);
        Point3D mid = origin.midpoint(target);
        cylinder.getTransforms().add(new Translate(mid.getX(), mid.getY(), mid.getZ()));

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D axisOfRotation = yAxis.crossProduct(diff);
        double angle = Math.acos(yAxis.normalize().dotProduct(diff.normalize())) * 180 / Math.PI;
        if (!axisOfRotation.equals(Point3D.ZERO)) {
            cylinder.getTransforms().add(new Rotate(angle, axisOfRotation));
        }
        System.err.println(height + " ***** " + cylinder.getHeight() + " *** " + cylinder.getTranslateY());
    }

    @Override
    public void debug() {
        System.out.println("t = (" +
                            t.getX() + ", " +
                            t.getY() + ", " + 
                            t.getZ() + ") " +
                            "r = (" +
                            rx.getAngle() + ", " +
                            ry.getAngle() + ", " +
                            rz.getAngle() + ") " +
                            "s = (" +
                            s.getX() + ", " +
                            s.getY() + ", " + 
                            s.getZ() + ") " +
                            "p = (" +
                            p.getX() + ", " +
                            p.getY() + ", " + 
                            p.getZ() + ") " +
                            "ip = (" +
                            ip.getX() + ", " +
                            ip.getY() + ", " + 
                            ip.getZ() + ") " +
                            "ct = (" +
                            cylinder.getTranslateX() + ", " +
                            cylinder.getTranslateY() + ", " + 
                            cylinder.getTranslateZ() + ") " +
                            "cr = (a=" +
                            cylinder.getRotationAxis() + ", d=" +
                            cylinder.getRotate() + ") "
                            );
    }
}

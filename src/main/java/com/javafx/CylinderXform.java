package com.javafx;

import javafx.scene.shape.Cylinder;

public class CylinderXform extends Xform {
    public Cylinder cylinder;

    public CylinderXform() {
        super();
        cylinder = new Cylinder();
        this.getChildren().add(cylinder);
    }

    public CylinderXform(double radius, double height) {
        super();
        cylinder = new Cylinder(radius, height);
        cylinder.setTranslateY(height/2.0);
        // cylinder.setRotationAxis(Rotate.Z_AXIS);
        // cylinder.setRotate(90.0);
        this.getChildren().add(cylinder);
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

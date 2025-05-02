package com.javafx;

import javafx.scene.shape.Sphere;

public class SphereXform extends Xform {
    public Sphere sphere;

    public SphereXform() {
        super();
        sphere = new Sphere();
        this.getChildren().add(sphere);
    }

    public SphereXform(RotateOrder rotateOrder) { 
        super(rotateOrder);
        sphere = new Sphere();
        this.getChildren().add(sphere);
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
                            sphere.getTranslateX() + ", " +
                            sphere.getTranslateY() + ", " + 
                            sphere.getTranslateZ() + ") " +
                            "cr = (a=" +
                            sphere.getRotationAxis() + ", d=" +
                            sphere.getRotate() + ") "
                            );
    }
}

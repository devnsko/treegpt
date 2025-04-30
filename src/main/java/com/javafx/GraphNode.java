package com.javafx;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class GraphNode {
    public String id;
    public String parentId;
    public int cluster;
    public double x, y, z;
    public Sphere sphere;
    public Xform sphereXform;

    public GraphNode(String id, String parentId, int cluster, double x, double y, double z) {
        this.id = id;
        this.parentId = parentId;
        this.cluster = cluster;
        this.x = x;
        this.y = y;
        this.z = z;

        this.sphere = new Sphere(0.03);
        this.sphereXform = new Xform();
        this.sphereXform.setTranslate(x, y, z);
        this.sphereXform.getChildren().add(this.sphere);


        PhongMaterial mat = new PhongMaterial(GraphUtils.colorByCluster(cluster));
        this.sphere.setMaterial(mat);
    }

    public Sphere getSphere() {
        return this.sphere;
    }

    public Xform getXform() {
        return this.sphereXform;
    }

    public static GraphNode findById(java.util.List<GraphNode> list, String id) {
        return list.stream().filter(n -> n.id.equals(id)).findFirst().orElse(null);
    }
}

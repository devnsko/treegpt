package com.javafx;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class GraphNode {
    public String id;
    public String parentId;
    public int cluster;
    public double x, y, z;
    public Sphere sphere;

    public GraphNode(String id, String parentId, int cluster, double x, double y, double z) {
        this.id = id;
        this.parentId = parentId;
        this.cluster = cluster;
        this.x = x;
        this.y = y;
        this.z = z;

        this.sphere = new Sphere(5);
        this.sphere.setTranslateX(x * 50);
        this.sphere.setTranslateY(y * 50);
        this.sphere.setTranslateZ(z * 50);

        PhongMaterial mat = new PhongMaterial(GraphUtils.colorByCluster(cluster));
        this.sphere.setMaterial(mat);
    }

    public Sphere getSphere() {
        return this.sphere;
    }

    public static GraphNode findById(java.util.List<GraphNode> list, String id) {
        return list.stream().filter(n -> n.id.equals(id)).findFirst().orElse(null);
    }
}

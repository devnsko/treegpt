package com.javafx;

import java.util.List;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class GraphNode {
    @Deprecated
    public String id;
    public String promptId;
    public String replyId;
    public List<String> childrenIds;
    public String parentId;
    public String conversationTitle;
    public String conversationId;
    public int cluster;
    public double x, y, z;
    public Sphere sphere;
    public Xform sphereXform;

    public GraphNode(String promptId, String replyId, List<String> childrenIds, String parentId, String conversationTitle, String conversationId, int cluster, double x, double y, double z) {
        this.promptId = promptId;
        this.replyId = replyId;
        this.childrenIds = childrenIds;
        this.parentId = parentId;
        this.conversationTitle = conversationTitle;
        this.conversationId = conversationId;
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

    public static GraphNode findByPromptId(List<GraphNode> list, String id) {
        return list.stream().filter(n -> n.promptId.equals(id)).findFirst().orElse(null);
    }

    public static GraphNode findByReplyId(List<GraphNode> list, String id) {
        return list.stream().filter(n -> n.replyId.equals(id)).findFirst().orElse(null);
    }
}

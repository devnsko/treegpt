package com.javafx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.Random;

public class GraphUtils {
    public static Color colorByCluster(int cluster) {
        Random rand = new Random(cluster * 99991);
        return Color.hsb(rand.nextInt(360), 0.9, 0.8);
    }

    public static Line connect(GraphNode a, GraphNode b) {
        Line line = new Line();
        line.setStartX(a.x * 50);
        line.setStartY(a.y * 50);
        line.setEndX(b.x * 50);
        line.setEndY(b.y * 50);
        return line;
    }
}

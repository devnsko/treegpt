package com.javafx;

import javafx.scene.shape.Line;

public class LineXform extends Xform {
    public Line line;

    public LineXform() {
        super();
        line = new Line();
        this.getChildren().add(line);
    }

    public Line getLine() {
        return line;
    }
}

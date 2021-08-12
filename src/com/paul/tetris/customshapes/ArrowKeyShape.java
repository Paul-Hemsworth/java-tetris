package com.paul.tetris.customshapes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ArrowKeyShape extends Group {
    public final DoubleProperty size = new SimpleDoubleProperty(1.0);
    private final double ARROW_SIZE_FRACTION = 0.75;
    private final double CORNER_ARC_SIZE_FRACTION = 0.25;
    private final Arrow ARROW;
    private final Rectangle RECTANGLE;

    public ArrowKeyShape(double size){
        super();
        getChildren().addAll(RECTANGLE = new Rectangle(size, size), ARROW = new Arrow(size*ARROW_SIZE_FRACTION));
        ARROW.setTranslateX((1.0 - ARROW_SIZE_FRACTION) / 2 * size);
        ARROW.setTranslateY((1.0 - ARROW_SIZE_FRACTION) / 2 * size);
        RECTANGLE.setArcWidth(size*CORNER_ARC_SIZE_FRACTION);
        RECTANGLE.setArcHeight(size*CORNER_ARC_SIZE_FRACTION);
        RECTANGLE.setFill(Color.GREEN);
        ARROW.setFill(Color.WHITE);
        this.size.set(size);
    }

    public ArrowKeyShape(){
        this(1.0);
    }

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public Rectangle getRectangle(){
        return RECTANGLE;
    }

    public Arrow getArrow(){
        return ARROW;
    }

    public void setSize(double size){
        RECTANGLE.setWidth(size);
        RECTANGLE.setHeight(size);
        ARROW.setSize(size*ARROW_SIZE_FRACTION);
        ARROW.setTranslateX((1.0 - ARROW_SIZE_FRACTION) / 2 * size);
        ARROW.setTranslateY((1.0 - ARROW_SIZE_FRACTION) / 2 * size);
        RECTANGLE.setArcWidth(size*CORNER_ARC_SIZE_FRACTION);
        RECTANGLE.setArcHeight(size*CORNER_ARC_SIZE_FRACTION);
        this.size.set(size);
    }
}

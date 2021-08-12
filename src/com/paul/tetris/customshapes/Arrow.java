package com.paul.tetris.customshapes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Polygon;

public class Arrow extends Polygon {
    public final DoubleProperty size = new SimpleDoubleProperty(1.0);

    // Size and direction specified by arguments.
    // Direction is the clockwise angle in degrees from the x-axis
    public Arrow(double size, double direction){
        super();
        setSize(size);
        setRotate(direction);
    }

    // Size specified by argument. Direction along x-axis
    public Arrow(double size){
        super();
        setSize(size);
    }

    // Size of 1.0 along x-axis
    public Arrow(){
        super(0.0, 0.35, 0, 0.65, 0.5, 0.65, 0.5, 1.0, 1.0, 0.5, 0.5, 0.0, 0.5, 0.35);
    }

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size){
        final Double[] UNIT_PTS = {0.0, 0.35, 0.0, 0.65, 0.5, 0.65, 0.5, 1.0, 1.0, 0.5, 0.5, 0.0, 0.5, 0.35};

        int idx = 0;
        while (idx < UNIT_PTS.length){
            UNIT_PTS[idx] = UNIT_PTS[idx]*size;
            idx++;
        }
        getPoints().setAll(UNIT_PTS);
        this.size.set(size);
    }
}
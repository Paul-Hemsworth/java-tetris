package com.paul.tetris.customshapes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import java.util.EnumMap;
import java.util.Map;

public class KeyShape extends Group {
    public final DoubleProperty size = new SimpleDoubleProperty(1.0);
    public final ObjectProperty<KeyCode> key = new SimpleObjectProperty<>();
    private final double SHAPE_SIZE_FRACTION = 0.75;
    private final double CORNER_ARC_SIZE_FRACTION = 0.25;
    private Shape shape = null;
    private final Rectangle RECTANGLE;

    private static final Map.Entry<KeyCode, Shape> LEFT_KEY = Map.entry(KeyCode.LEFT, new Arrow(1.0, 180.0));
    private static final Map.Entry<KeyCode, Shape> RIGHT_KEY = Map.entry(KeyCode.RIGHT, new Arrow(1.0));
    private static final Map.Entry<KeyCode, Shape> UP_KEY = Map.entry(KeyCode.UP, new Arrow(1.0, 270.0));
    private static final Map.Entry<KeyCode, Shape> DOWN_KEY = Map.entry(KeyCode.DOWN, new Arrow(1.0, 90.0));
    private static final Map.Entry<KeyCode, Shape> P_KEY = Map.entry(KeyCode.P, new Text("P"));

    private static final EnumMap<KeyCode, Shape> KEYCODE_SHAPE_MAP = new EnumMap<>(Map.ofEntries(
            LEFT_KEY, RIGHT_KEY, UP_KEY, DOWN_KEY, P_KEY
    ));

    public KeyShape(KeyCode kc, double size){
        super();
        getChildren().add(RECTANGLE = new Rectangle(size, size));
        setSize(size);
        setKey(kc);
    }

    public KeyShape(){
        this(null, 1.0);
    }

    public KeyCode getKey() {
        return key.get();
    }

    public ObjectProperty<KeyCode> keyProperty() {
        return key;
    }

    public void setKey(KeyCode key){
        if ((shape = KEYCODE_SHAPE_MAP.get(key)) != null){
            getChildren().add(shape);
            shape.setScaleX(size.get());
            shape.setScaleY(size.get());
        }
        this.key.set(key);
    }

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size){
        RECTANGLE.setWidth(size);
        RECTANGLE.setHeight(size);
        shape.setTranslateX((1.0 - SHAPE_SIZE_FRACTION) / 2 * size);
        shape.setTranslateY((1.0 - SHAPE_SIZE_FRACTION) / 2 * size);
        RECTANGLE.setArcWidth(size*CORNER_ARC_SIZE_FRACTION);
        RECTANGLE.setArcHeight(size*CORNER_ARC_SIZE_FRACTION);
        this.size.set(size);
    }

    public Rectangle getRectangle(){
        return RECTANGLE;
    }

    public Shape getShape(){
        return shape;
    }
}

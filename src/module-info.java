module com.paul.tetris {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens com.paul.tetris.fxmlcontrollers to javafx.fxml, javafx.base;
    opens com.paul.tetris.customshapes to javafx.fxml;
    exports com.paul.tetris to javafx.graphics;
    exports com.paul.tetris.fxmlcontrollers;
    exports com.paul.tetris.game;
}
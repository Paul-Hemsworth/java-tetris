package com.paul.tetris.fxmlcontrollers;

import com.paul.tetris.TetrisMain;
import com.paul.tetris.customshapes.ArrowKeyShape;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;

public class HelpController {
    private final TetrisMain TETRIS_MAIN;
    private int lastScene = TetrisMain.MENU_SCENE;
    @FXML
    private BorderPane helpBorderPane;
    @FXML
    private ArrowKeyShape leftArrow, rightArrow, upArrow, downArrow;

    public HelpController(TetrisMain tetrisMain){
        TETRIS_MAIN = tetrisMain;
    }

    @FXML
    public void initialize(){
        ChangeListener<Number> paneResizeListener = (obsVal, oldNum, newNum) -> {
            final double RECT_SIZE = helpBorderPane.getWidth()/10;
            leftArrow.setSize(RECT_SIZE);
            leftArrow.setTranslateY(RECT_SIZE);
            rightArrow.setSize(RECT_SIZE);
            rightArrow.setTranslateY(RECT_SIZE);
            rightArrow.setTranslateX(2*RECT_SIZE);
            upArrow.setSize(RECT_SIZE);
            upArrow.setTranslateX(RECT_SIZE);
            downArrow.setSize(RECT_SIZE);
            downArrow.setTranslateY(RECT_SIZE);
            downArrow.setTranslateX(RECT_SIZE);
        };

        helpBorderPane.widthProperty().addListener(paneResizeListener);
        helpBorderPane.heightProperty().addListener(paneResizeListener);
    }

    @FXML
    private void handleBack(){
        TETRIS_MAIN.changeScene(lastScene);
    }

    void setLast(int scene){
        lastScene = scene;
    }
}
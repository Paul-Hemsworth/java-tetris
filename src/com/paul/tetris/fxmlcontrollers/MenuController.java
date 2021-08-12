package com.paul.tetris.fxmlcontrollers;

import com.paul.tetris.TetrisMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.beans.value.ChangeListener;

public class MenuController {
    public VBox menuVBox;
    public Label titleLabel;
    private final TetrisMain TETRIS_MAIN;
    @FXML
    private Button playButton, helpButton, scoresButton, exitButton;

    public MenuController(TetrisMain tetrisMain){
        TETRIS_MAIN = tetrisMain;
    }

    @FXML
    public void initialize(){
        ChangeListener<Number> vBoxWidthListener = (obsVal, oldNum, newNum) -> {
            final double BUTTON_WIDTH = newNum.doubleValue()/1.333;
            playButton.setPrefWidth(BUTTON_WIDTH);
            helpButton.setPrefWidth(BUTTON_WIDTH);
            scoresButton.setPrefWidth(BUTTON_WIDTH);
            exitButton.setPrefWidth(BUTTON_WIDTH);
        };

        ChangeListener<Number> vBoxHeightListener = (obsVal, oldNum, newNum) -> {
            final double MIN_BTN_FONT_SIZE = 22; // px (14pt)
            final double BUTTON_HEIGHT = newNum.doubleValue()/6;
            double newBtnFontSize = MIN_BTN_FONT_SIZE*newNum.doubleValue()/TETRIS_MAIN.getMinHeight();

            playButton.setPrefHeight(BUTTON_HEIGHT);
            helpButton.setPrefHeight(BUTTON_HEIGHT);
            scoresButton.setPrefHeight(BUTTON_HEIGHT);
            exitButton.setPrefHeight(BUTTON_HEIGHT);

            playButton.setStyle("-fx-font-size: " + newBtnFontSize + ";");
            helpButton.setStyle("-fx-font-size: " + newBtnFontSize + ";");
            scoresButton.setStyle("-fx-font-size: " + newBtnFontSize + ";");
            exitButton.setStyle("-fx-font-size: " + newBtnFontSize + ";");
            titleLabel.setStyle("-fx-font-size: " + BUTTON_HEIGHT + ";");
        };

        menuVBox.widthProperty().addListener(vBoxWidthListener);
        menuVBox.heightProperty().addListener(vBoxHeightListener);
    }

    @FXML
    private void handlePlay(){
        TETRIS_MAIN.getGameController().startNewGame();
    }

    @FXML
    private void handleHelp() {
        TETRIS_MAIN.changeScene(TetrisMain.HELP_SCENE);
        TETRIS_MAIN.getHelpController().setLast(TetrisMain.MENU_SCENE);
    }

    @FXML
    private void handleScores() {
        TETRIS_MAIN.changeScene(TetrisMain.SCORE_SCENE);
    }

    @FXML
    private void handleExit(){
        Platform.exit();
    }
}
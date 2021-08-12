package com.paul.tetris.fxmlcontrollers;

import com.paul.tetris.TetrisMain;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class PauseController {
    private final TetrisMain TETRIS_MAIN;
    @FXML
    private BorderPane borderPane;

    public PauseController(TetrisMain tetrisMain){
        TETRIS_MAIN = tetrisMain;
    }

    @FXML
    public void initialize(){
        borderPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.P)){
                handleResume();
            }
            keyEvent.consume();
        });
    }

    @FXML
    private void handleResume(){
        TETRIS_MAIN.getGameController().resume();
    }

    @FXML
    private void handleHelp(){
        TETRIS_MAIN.changeScene(TetrisMain.HELP_SCENE);
        TETRIS_MAIN.getHelpController().setLast(TetrisMain.PAUSE_SCENE);
    }

    @FXML
    private void handleEndGame(){
        TETRIS_MAIN.getGameController().endGame();
    }
}

package com.paul.tetris.fxmlcontrollers;

import com.paul.tetris.TetrisMain;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EndGameController{
    private final int MAX_NAME_LENGTH = 50;
    private final TetrisMain TETRIS_MAIN;
    private int score;
    @FXML
    private TextField nameTextField;
    @FXML
    private CheckBox saveCheckBox;
    @FXML
    private Label finalScoreText;

    public EndGameController(TetrisMain tetrisMain){
        TETRIS_MAIN = tetrisMain;
    }

    @FXML
    public void initialize(){


        // Select the check box automatically if there is text
        nameTextField.textProperty().addListener((obs, oldText, newText)->{
            saveCheckBox.setSelected(!newText.strip().equals(""));
            if (newText.length() > MAX_NAME_LENGTH){
                nameTextField.setText(newText.substring(0, MAX_NAME_LENGTH));
            }
        });
    }

    public void showFinalScore(int score) {
        finalScoreText.setText("You scored " + score + " points!");
        this.score = score;
    }

    @FXML
    private void handleNewGame(){
        saveScoreAndResetTextInputs();
        TETRIS_MAIN.getGameController().startNewGame();
    }

    @FXML
    private void handleMainMenu(){
        saveScoreAndResetTextInputs();
        TETRIS_MAIN.changeScene(TetrisMain.MENU_SCENE);
    }

    private void saveScoreAndResetTextInputs(){
        if (saveCheckBox.selectedProperty().get()){
            String name = nameTextField.getText().strip();
            TETRIS_MAIN.getScoresController().addScoreEntry(name, score);
        }
        nameTextField.clear();
        nameTextField.requestFocus();
        finalScoreText.setText("0");
    }
}

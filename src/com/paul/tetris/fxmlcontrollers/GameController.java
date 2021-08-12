package com.paul.tetris.fxmlcontrollers;

import com.paul.tetris.TetrisMain;
import com.paul.tetris.game.Tetriminos;
import com.paul.tetris.game.Game;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameController{
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label scoreLabel, nextPieceLabel;
    public Group gridGroup, nextPieceGroup;
    private Game tetrisGame;
    private final TetrisMain TETRIS_MAIN;
    private final int WIDTH, HEIGHT;
    private final Color[] BLOCK_FILLS = new Color[]{Color.LIGHTGREY, // Empty blocks
            Color.SKYBLUE,  // I
            Color.GOLD,     // O
            Color.DARKBLUE, // J
            Color.ORANGE,   // L
            Color.GREEN,    // S
            Color.PURPLE,   // T
            Color.RED       // Z
    };

    public GameController(int width, int height, TetrisMain main){
        WIDTH = width;
        HEIGHT = height;
        TETRIS_MAIN = main;
    }

    @FXML
    private void initialize() {
        // Size grid squares
        final double SQUARE_DIMENSION;
        final double PANE_WIDTH = borderPane.getWidth();
        final double PANE_HEIGHT = borderPane.getHeight();
        if (PANE_WIDTH >= (PANE_HEIGHT*(WIDTH+6)/(HEIGHT + 1))){
            // Size based on height
            SQUARE_DIMENSION = PANE_HEIGHT/(HEIGHT+1);
        } else {
            // Size based on width
            SQUARE_DIMENSION = PANE_WIDTH/(WIDTH + 6);
        }

        // Generate the grid rectangles
        Rectangle[] blocks = new Rectangle[WIDTH*HEIGHT];
        for (int row = 0; row < HEIGHT; row++){
            for (int column = 0; column < WIDTH; column++){
                double x = SQUARE_DIMENSION*column;
                double y = SQUARE_DIMENSION*row;
                Rectangle rectangle = new Rectangle(x, y, SQUARE_DIMENSION, SQUARE_DIMENSION);
                rectangle.setFill(BLOCK_FILLS[0]);
                rectangle.setStroke(Color.WHITE);
                rectangle.setStrokeWidth(0.5d);
                blocks[row*WIDTH + column] = rectangle;
            }
        }
        gridGroup.getChildren().setAll(blocks);

        // Generate the next piece rectangles
        blocks = new Rectangle[16];
        for (int row = 0; row < 4; row++){
            for (int column = 0; column < 4; column++){
                double x = SQUARE_DIMENSION*column;
                double y = SQUARE_DIMENSION*row;
                Rectangle rectangle = new Rectangle(x, y, SQUARE_DIMENSION, SQUARE_DIMENSION);
                rectangle.setFill(BLOCK_FILLS[0]);
                rectangle.setStroke(Color.WHITE);
                rectangle.setStrokeWidth(0.5d);
                blocks[row*4 + column] = rectangle;
            }
        }
        nextPieceGroup.getChildren().setAll(blocks);

        // Recalculate rectangle sizes when borderpane is resized
        ChangeListener<Number> paneSizeListener = (obsVal, oldNum, newNum) -> {
            // Figure out the block size based on borderpane and tetris grid aspect ratios
            final double SQUARE_SIZE;
            final double BP_WIDTH = borderPane.getWidth();
            final double BP_HEIGHT = borderPane.getHeight();
            if (BP_WIDTH >= (BP_HEIGHT*(WIDTH+6)/(HEIGHT+1))){
                // Size based on height
                SQUARE_SIZE = BP_HEIGHT/(HEIGHT+1);
            } else {
                // Size based on width
                SQUARE_SIZE = BP_WIDTH/(WIDTH+6);
            }

            // Resize gridGroup squares
            gridGroup.setTranslateX(SQUARE_SIZE/2);
            int idx = 0;
            for (Node n : gridGroup.getChildren()){
                ((Rectangle) n).setWidth(SQUARE_SIZE);
                ((Rectangle) n).setHeight(SQUARE_SIZE);
                ((Rectangle) n).setX(((double) (idx%WIDTH))*SQUARE_SIZE);
                ((Rectangle) n).setY(((double) (idx/WIDTH))*SQUARE_SIZE);
                idx++;
            }

            // Resize nextPieceGroup squares
            idx = 0;
            for (Node n : nextPieceGroup.getChildren()){
                ((Rectangle) n).setWidth(SQUARE_SIZE);
                ((Rectangle) n).setHeight(SQUARE_SIZE);
                ((Rectangle) n).setX(((double) (idx%4))*SQUARE_SIZE);
                ((Rectangle) n).setY(((double) (idx/4))*SQUARE_SIZE);
                idx++;
            }
        };

        borderPane.widthProperty().addListener(paneSizeListener);
        borderPane.heightProperty().addListener(paneSizeListener);

        // Set event listener for keyboard inputs
        borderPane.setOnKeyPressed(keyEvent -> {
            tetrisGame.processKeyCode(keyEvent.getCode());
            keyEvent.consume();
        });
    }

    public synchronized void showNewTetrimino(Tetriminos newT, Tetriminos oldT){
        final Color EMPTY = BLOCK_FILLS[0];
        final Color FULL = BLOCK_FILLS[newT.ordinal() + 1];
        final int[] NEW_SHAPE = newT.getShape(0);
        final int[] OLD_SHAPE = oldT.getShape(0);

        final int OLD_SIZE = oldT.getSIZE();
        for (int idx = 0; idx < 4; idx++){
            int transformedIndex = 4*(OLD_SHAPE[idx]/OLD_SIZE) + OLD_SHAPE[idx]%OLD_SIZE;
            ((Rectangle) nextPieceGroup.getChildren().get(transformedIndex)).setFill(EMPTY);
        }

        final int NEW_SIZE = newT.getSIZE();
        for (int idx = 0; idx < 4; idx++){
            int transformedIndex = 4*(NEW_SHAPE[idx]/NEW_SIZE) + NEW_SHAPE[idx]%NEW_SIZE;
            ((Rectangle) nextPieceGroup.getChildren().get(transformedIndex)).setFill(FULL);
        }
    }

    public synchronized void fillBlocks(int[] indices, byte[] fills){
        int idx = 0;
        while (idx < indices.length){
            Color fill = BLOCK_FILLS[fills[idx]];
            ((Rectangle) gridGroup.getChildren().get(indices[idx])).setFill(fill);
            idx++;
        }
    }

    public synchronized void fillBlocks(int[] indices, byte fill){
        final Color FILL = BLOCK_FILLS[fill];
        int idx = 0;
        while (idx < indices.length){
            ((Rectangle) gridGroup.getChildren().get(indices[idx])).setFill(FILL);
            idx++;
        }
    }

    public synchronized void clearBlocks(int[] indices){
        fillBlocks(indices, (byte) 0);
    }

    public void setScoreText(int score){
        scoreLabel.setText(String.valueOf(score));
    }

    public void pause(){
        if (tetrisGame.isPlaying()) {
            tetrisGame.processAction(Game.PLAY_PAUSE);
        }
    }

    public void resume(){
        if (tetrisGame.isPlaying()){
            return;
        }
        tetrisGame.processAction(Game.PLAY_PAUSE);
    }

    public void endGame(){
        tetrisGame.processAction(Game.END_GAME);
    }

    void startNewGame(){
        // Clear tetris grid
        for (Node r: gridGroup.getChildren()){
            ((Rectangle) r).setFill(BLOCK_FILLS[0]);
        }

        // Clear next piece
        for (Node r: nextPieceGroup.getChildren()){
            ((Rectangle) r).setFill(BLOCK_FILLS[0]);
        }

        borderPane.requestFocus();
        scoreLabel.setText("0");
        tetrisGame = new Game(WIDTH, HEIGHT, this);
        TETRIS_MAIN.changeScene(TetrisMain.GAME_SCENE);
    }

    public TetrisMain getTetrisMain(){
        return TETRIS_MAIN;
    }
}
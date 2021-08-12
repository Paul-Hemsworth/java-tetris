package com.paul.tetris;

import com.paul.tetris.fxmlcontrollers.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import static java.lang.Math.ceil;

public class TetrisMain extends Application {
    private Stage window;
    private final int TETRIS_WIDTH = 10, TETRIS_HEIGHT = 20;
    private final double MIN_WIDTH = 228, MIN_HEIGHT = 299.25;
    private final Scene[] scenes = new Scene[7];
    private final EndGameController END_GAME_CONTROLLER;
    private final GameController GAME_CONTROLLER;
    private final HelpController HELP_CONTROLLER;
    private final MenuController MENU_CONTROLLER;
    private final PauseController PAUSE_CONTROLLER;
    private final ScoresController SCORES_CONTROLLER;

    public static final int
        END_GAME_SCENE  = 0,
        GAME_SCENE      = 1,
        HELP_SCENE      = 2,
        MENU_SCENE      = 3,
        PAUSE_SCENE     = 4,
        SCORE_SCENE     = 5;

    public TetrisMain(){
        // Construct the fxml controllers
        END_GAME_CONTROLLER = new EndGameController(this);
        GAME_CONTROLLER     = new GameController(TETRIS_WIDTH, TETRIS_HEIGHT, this);
        HELP_CONTROLLER     = new HelpController(this);
        MENU_CONTROLLER     = new MenuController(this);
        PAUSE_CONTROLLER    = new PauseController(this);
        SCORES_CONTROLLER   = new ScoresController(this);
    }

    @Override
    public void start(Stage window){
        // Determine the scene height and width based on screen aspect ratio
        final double SCENE_WIDTH, SCENE_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT, WIDTH_IN_BLOCKS, HEIGHT_IN_BLOCKS;
        SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
        SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
        WIDTH_IN_BLOCKS = TETRIS_WIDTH + 6;
        HEIGHT_IN_BLOCKS = TETRIS_HEIGHT + 1;

        if (SCREEN_WIDTH >= (SCREEN_HEIGHT*WIDTH_IN_BLOCKS/HEIGHT_IN_BLOCKS)) { // Size based on height
            SCENE_HEIGHT = ceil(SCREEN_HEIGHT * 0.9);
            SCENE_WIDTH = ceil(SCREEN_HEIGHT * 0.9 * WIDTH_IN_BLOCKS / HEIGHT_IN_BLOCKS);
        } else { // Size based on width
            SCENE_WIDTH = ceil(SCREEN_WIDTH * 0.9);
            SCENE_HEIGHT = ceil(SCREEN_WIDTH * 0.9 * HEIGHT_IN_BLOCKS/WIDTH_IN_BLOCKS);
        }

        // Arrays to loop through when creating scenes
        final Object[] CONTROLLERS = {END_GAME_CONTROLLER, GAME_CONTROLLER, HELP_CONTROLLER,
                MENU_CONTROLLER, PAUSE_CONTROLLER, SCORES_CONTROLLER};
        final String[] FXML_FILES = {"endGame.fxml", "game.fxml", "help.fxml",
                "menu.fxml", "pause.fxml", "scores.fxml"};
        FXMLLoader loader;

        try {
            for (int k = 0; k < CONTROLLERS.length; k++) {
                loader = new FXMLLoader(getClass().getResource("/fxml/" + FXML_FILES[k]));
                loader.setController(CONTROLLERS[k]);
                scenes[k] = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);
            }
        } catch (IOException | IllegalStateException e){
            System.err.println("IOException: Unable to load all scenes.");
            Platform.exit();
        }

        // Configure window
        this.window = window;
        window.setTitle("Tetris");
        window.setScene(scenes[MENU_SCENE]);

        // Set window icons
        try {
            ObservableList<Image> windowIcons = window.getIcons();
            windowIcons.add(new Image(Objects.requireNonNull(getClass().getResource("/img/icon16.png")).toString()));
            windowIcons.add(new Image(Objects.requireNonNull(getClass().getResource("/img/icon32.png")).toString()));
            windowIcons.add(new Image(Objects.requireNonNull(getClass().getResource("/img/icon64.png")).toString()));
            windowIcons.add(new Image(Objects.requireNonNull(getClass().getResource("/img/icon128.png")).toString()));
        } catch (NullPointerException e){
            System.err.println("NullPointerException: Unable to add all window icon images.");
        }

        window.show();
        // Setting the stage size forces all scenes to size to the stage even after resizing the stage
        window.setWidth(window.getWidth());
        window.setHeight(window.getHeight());

        // Calculate window insets and set minimum window size
        final double TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET;
        LEFT_INSET = window.getScene().getX();
        TOP_INSET = window.getScene().getY();
        RIGHT_INSET = window.getWidth() - window.getScene().getWidth() - LEFT_INSET;
        BOTTOM_INSET = window.getHeight() - window.getScene().getHeight() - TOP_INSET;
        window.setMinWidth(MIN_WIDTH + LEFT_INSET + RIGHT_INSET);
        window.setMinHeight(MIN_HEIGHT + TOP_INSET + BOTTOM_INSET);
    }

    public void changeScene(int scene){
        if ((scene >= 0) && (scene < scenes.length)) {
            window.setScene(scenes[scene]);
        }
    }

    public EndGameController getEndGameController(){
        return END_GAME_CONTROLLER;
    }

    public GameController getGameController(){
        return GAME_CONTROLLER;
    }

    public HelpController getHelpController(){
        return HELP_CONTROLLER;
    }

    public MenuController getMenuController(){
        return MENU_CONTROLLER;
    }

    public PauseController getPauseController(){
        return PAUSE_CONTROLLER;
    }

    public ScoresController getScoresController(){
        return SCORES_CONTROLLER;
    }

    public double getMinWidth(){
        return MIN_WIDTH;
    }

    public double getMinHeight(){
        return MIN_HEIGHT;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

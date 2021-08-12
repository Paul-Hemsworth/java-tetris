package com.paul.tetris.game;

import com.paul.tetris.TetrisMain;
import com.paul.tetris.fxmlcontrollers.GameController;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import java.util.Timer;
import java.util.TimerTask;

/********************************************************************************
 * This class controls the game
 *******************************************************************************/
public class Game implements Runnable{
    public static final int END_GAME   = -3;
    public static final int PLAY_PAUSE = -2;
    static final int LEFT       = -1;
    static final int DOWN       =  0;
    static final int RIGHT      =  1;
    static final int ROTATE     =  2;
    static final int SPAWN      =  3;
    private int action = PLAY_PAUSE, score = 0;
    private boolean playing = false, notDown = true;
    private final GameController gameController;
    private final Grid grid;
    private Timer timer;
    private Tetriminos nextTetrimino = Tetriminos.getRandom();

    public Game(int gridWidth, int gridHeight, GameController gameController){
        this.gameController = gameController;
        grid = new Grid(gridWidth, gridHeight, this, gameController);
        Thread tetrisThread = new Thread(this, "Tetris_Thread");
        tetrisThread.setDaemon(true); //This thread should end when the main thread does
        tetrisThread.start();
    }

    //This is for the thread where the game is run
    public synchronized void run(){
        playPause();
        while (action != END_GAME){
            try{
                wait();
                //System.out.println(Thread.currentThread().getName() + " Notified " + action);
            } catch (InterruptedException e){
                System.err.println("The Tetris Thread was Interrupted");
                action = END_GAME;
            }

            if (playing){
                switch (action){
                    case LEFT:
                    case RIGHT:
                    case DOWN:
                        if (grid.getActivePiece().translateTetrimino(action)){
                            spawn();
                        }
                        break;
                    case ROTATE:
                        grid.getActivePiece().rotateTetrimino();
                        break;
                    case SPAWN:
                        spawn();
                        break;
                }
            }

            if (action == PLAY_PAUSE){
                playPause();
            }
        }
        Platform.runLater(()->{
            TetrisMain tetrisMain = gameController.getTetrisMain();
            tetrisMain.getEndGameController().showFinalScore(score);
            tetrisMain.changeScene(TetrisMain.END_GAME_SCENE);
        });
    }

    /********************************************************************************
     * Accepts an integer argument corresponding to one of the constants defined at
     * the beginning of this class and notifies Tetris_Thread to perform the action
     *******************************************************************************/
    public synchronized void processAction(int action){
        if ((action >= END_GAME) && (action <= SPAWN)){
            this.action = action;
            notifyAll();
        }
    }

    /********************************************************************************
     * Maps key presses to game actions and notifies all threads so that Tetris_Thread
     * can execute the appropriate method
     *******************************************************************************/
    public synchronized void processKeyCode(KeyCode keyCode){
        boolean validKeyPress = true;
        switch (keyCode) {
            case LEFT:
                action = Game.LEFT;
                break;
            case RIGHT:
                action = Game.RIGHT;
                break;
            case UP:
                action = Game.ROTATE;
                break;
            case DOWN:
                action = Game.DOWN;
                notDown = false;
                break;
            case P:
                action = PLAY_PAUSE;
                break;
            default:
                validKeyPress = false;
                break;
        }
        // Only notify for valid KeyCodes, ignore otherwise
        if(validKeyPress){
            notifyAll();
        }
    }

    private synchronized void spawn(){
        if (grid.getActivePiece().spawnTetrimino(nextTetrimino)){
            playing = false;
            action = END_GAME;
        } else{
            final Tetriminos OLD_TETRIMINO = nextTetrimino;
            nextTetrimino = Tetriminos.getRandom();
            Platform.runLater(()-> gameController.showNewTetrimino(nextTetrimino, OLD_TETRIMINO));
        }
    }

    public int getScore(){
        return score;
    }

    public boolean isPlaying(){
        return playing;
    }

    void updateScore(int increase){
        score += increase*increase; //More points for more rows cleared in single move
        Platform.runLater(()-> gameController.setScoreText(score));
    }

    private synchronized void playPause(){
        if (playing){
            playing = false;
            timer.cancel();
            Platform.runLater(()->gameController.getTetrisMain().changeScene(TetrisMain.PAUSE_SCENE));
        } else{
            playing = true;

            if (grid.getActivePiece().noTetrimino()){
                spawn();
            }

            Platform.runLater(()->gameController.getTetrisMain().changeScene(TetrisMain.GAME_SCENE));
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (playing){
                        if (notDown) {
                            processAction(DOWN);
                        }
                        notDown = true;
                    } else{
                        cancel();
                    }
                }
            }, 1000, 1000);
        }
    }
}

package com.paul.tetris.game;

import com.paul.tetris.fxmlcontrollers.GameController;
import javafx.application.Platform;
import java.util.Arrays;

public class Grid {
    private final byte GRID_FILL = 0;
    private final byte[] grid;
    private final int WIDTH;
    private final int HEIGHT;
    private final Grid.ActivePiece ACTIVE_PIECE;
    private final GameController guiController;
    private final Game TETRIS_GAME;

    public Grid(int width, int height, Game tetris, GameController controller){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.TETRIS_GAME = tetris;
        this.guiController = controller;
        grid = new byte[WIDTH*HEIGHT];
        ACTIVE_PIECE = new Grid.ActivePiece();
    }

    ActivePiece getActivePiece(){
        return ACTIVE_PIECE;
    }

    class ActivePiece {
        private int position = 0, rotation = 0;
        private int[] lastIndices = new int[4];
        private Tetriminos currentTetrimino;

        boolean noTetrimino(){
            return currentTetrimino == null;
        }

        /****************************************************************************
         * Positions new tetrimino and locks the other one in place
         * Returns true if the block can be spawned. Game over when it returns false
         ***************************************************************************/
        boolean spawnTetrimino(Tetriminos t){
            final int[] SHAPE = t.getShape(rotation = 0);
            position = (WIDTH - t.getSIZE()) / 2 - (SHAPE[0] / t.getSIZE()) * WIDTH;
            final int[] NEW_INDICES = transformIndicesToGrid(SHAPE, t.getSIZE(), position);

            checkTetris();
            if (isValidMove(NEW_INDICES, Game.SPAWN)){
                currentTetrimino = t;
                drawTetrimino(NEW_INDICES, false);
                return false; // Game over = false
            }
            return true; // Game over = true
        }

        /****************************************************************************
         * Requires a Tetrimino, position index and rotation index
         * Determines if the tetrimino can be rotated without hitting the wall or
         * another block
         * If the block can be rotated, the grid is updated
         * The rotation index is always returned regardless of rotation change
         ***************************************************************************/
        void rotateTetrimino(){
            //Handle special case for O block
            if (currentTetrimino.getROTATIONS() == 0){
                return;
            }

            final int NEW_ROTATION = (rotation + 1)%currentTetrimino.getROTATIONS();
            final int[] NEW_INDICES = transformIndicesToGrid(currentTetrimino.getShape(NEW_ROTATION), currentTetrimino.getSIZE(), position);

            if (isValidMove(NEW_INDICES, Game.ROTATE)){
                drawTetrimino(NEW_INDICES, true);
                rotation = NEW_ROTATION;
            }
        }

        /****************************************************************************
         * Requires the current tetrimino, its position, its rotation, and a direction
         * Changes the position index if the translation is not blocked by a wall
         * or another block
         * Updates grid display if a change occurs
         * Always returns a position index regardless of a change occuring or not
         ***************************************************************************/
        boolean translateTetrimino(int direction){
            final int[] NEW_INDICES = Arrays.copyOf(lastIndices, 4);
            int finalPosition = position, indexChange;

            switch (direction){
                case Game.LEFT:
                    indexChange = -1;
                    break;
                case Game.RIGHT:
                    indexChange = 1;
                    break;
                case Game.DOWN:
                    indexChange = WIDTH;
                    break;
                default:
                    indexChange = 0;
            }
            finalPosition += indexChange;

            NEW_INDICES[0] += indexChange;
            NEW_INDICES[1] += indexChange;
            NEW_INDICES[2] += indexChange;
            NEW_INDICES[3] += indexChange;

            if (isValidMove(NEW_INDICES, direction)){
                drawTetrimino(NEW_INDICES, true);
                lastIndices = NEW_INDICES;
                position = finalPosition;
                return false; // Don't spawn another block
            }

            return direction == Game.DOWN; // Return true to spawn another block
        }
    }

    /****************************************************************************
     * Requires shape indices, tetrimino reference square size and grid offset
     * Modifies indices so that it is in grid space
     ***************************************************************************/
    private int[] transformIndicesToGrid(int[] indices, int size, int offset){
        final int IDX0 = (indices[0]/size)*WIDTH + (indices[0]%size) + offset;
        final int IDX1 = (indices[1]/size)*WIDTH + (indices[1]%size) + offset;
        final int IDX2 = (indices[2]/size)*WIDTH + (indices[2]%size) + offset;
        final int IDX3 = (indices[3]/size)*WIDTH + (indices[3]%size) + offset;
        return new int[]{IDX0, IDX1, IDX2, IDX3};
    }

    /****************************************************************************
     * Requires the new indices after a proposed translation or rotation and the
     * type of change to determine whether that operation can be done
     * Returns true if the operation is not blocked, false otherwise
     ***************************************************************************/
    private boolean isValidMove(int[] newIndices, int change){
        //System.out.printf("isValidMove:\n\tnewIndices: %s\n\tChange:%d\n", Arrays.toString(newIndices), change);

        int lowColumnIndex = WIDTH - 1, highColumnIndex = 0;
        final int[] lastIndices = ACTIVE_PIECE.lastIndices;

        for (int idx = 0; idx < 4; idx++){
            int value = newIndices[idx];

            //Test that the idx is in the bounds of the grid
            if ((value < 0) || (value >= WIDTH*HEIGHT)){
                return false;
            }

            //Test if shape rolls over into next row
            if (change== Game.ROTATE){
                //Special test for rotation
                int columnIndex = value%WIDTH;
                if (columnIndex > highColumnIndex){
                    highColumnIndex = columnIndex;
                }
                if (columnIndex < lowColumnIndex){
                    lowColumnIndex = columnIndex;
                }
            } else if ((change != Game.DOWN) && (change != Game.SPAWN) && ((value/WIDTH) != (lastIndices[idx]/WIDTH))){
                //Simpler test for translations
                return false;
            }

            //Test for clash with existing block
            if(Arrays.binarySearch(lastIndices, value) < 0 || change == Game.SPAWN){
                if (!(grid[value] == GRID_FILL)){
                    return false;
                }
            }
        }
        if (change == Game.ROTATE){
            return (highColumnIndex - lowColumnIndex) < 4; //4 is the max tetrimino size
        }

        return true;
    }

    public void drawTetrimino(int[] newIndices, boolean clearOld){
        final byte PIECE_FILL = (byte) (ACTIVE_PIECE.currentTetrimino.ordinal() + 1);
        final int IDX0 = newIndices[0];
        final int IDX1 = newIndices[1];
        final int IDX2 = newIndices[2];
        final int IDX3 = newIndices[3];

        //Clear activeTetrimino
        if (clearOld){
            final int[] OLD_INDICES = Arrays.copyOf(ACTIVE_PIECE.lastIndices, 4);

            grid[OLD_INDICES[0]] = GRID_FILL;
            grid[OLD_INDICES[1]] = GRID_FILL;
            grid[OLD_INDICES[2]] = GRID_FILL;
            grid[OLD_INDICES[3]] = GRID_FILL;

            Platform.runLater(()->guiController.clearBlocks(OLD_INDICES));
        }

        grid[IDX0] = PIECE_FILL;
        grid[IDX1] = PIECE_FILL;
        grid[IDX2] = PIECE_FILL;
        grid[IDX3] = PIECE_FILL;

        ACTIVE_PIECE.lastIndices[0] = IDX0;
        ACTIVE_PIECE.lastIndices[1] = IDX1;
        ACTIVE_PIECE.lastIndices[2] = IDX2;
        ACTIVE_PIECE.lastIndices[3] = IDX3;

        Platform.runLater(()->guiController.fillBlocks(newIndices, PIECE_FILL));
    }

    void checkTetris(){
        boolean fullRow = true;
        boolean emptyRow = true;
        int index = WIDTH*HEIGHT - 1;
        int count = 0;

        while(index >= 0){
            if (grid[index] == GRID_FILL) { //A rectangle is empty, thus the row cannot be full
                fullRow = false;
            } else { //A rectangle is filled and the row cannot be empty
                emptyRow = false;
            }

            if (index%WIDTH == 0){ //Row finished. Check if full or empty
                if(fullRow){
                    int shiftIndex = index + WIDTH - 1;
                    final int[] INDICES = new int[index]; // Indices of blocks to re-draw in FX Thread
                    final byte[] FILL_INDICES = new byte[index]; // Fill indices
                    count++;

                    while (shiftIndex >= WIDTH){
                        int rowUpIdx = shiftIndex - WIDTH;
                        grid[shiftIndex] = grid[rowUpIdx]; // Move rows down in grid
                        INDICES[rowUpIdx] = shiftIndex;
                        FILL_INDICES[rowUpIdx] = grid[shiftIndex];
                        shiftIndex--;
                    }

                    Platform.runLater(()->guiController.fillBlocks(INDICES, FILL_INDICES));
                    index = index + WIDTH; //Recheck the row after moving everything
                } else if (emptyRow){
                    break;
                }
                fullRow = true;
                emptyRow = true;
            }
            index--;
        }

        TETRIS_GAME.updateScore(count);
    }
}
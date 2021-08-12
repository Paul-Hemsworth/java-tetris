package com.paul.tetris.game;

public enum Tetriminos {
    I (4,0,2),
    O (2,2,0),
    J (3,3,4),
    L (3,7,4),
    S (3,11,2),
    T (3,13,4),
    Z (3,17,2);

    private final int SIZE, SHAPE_INDEX, ROTATIONS;
    private static final int[][] SHAPES = {
            //I
            {8,9,10,11},{2,6,10,14},
            //O
            {0,1,2,3},
            //J
            {3,4,5,8},{1,4,6,7},{0,3,4,5},{1,2,4,7},
            //L
            {3,4,5,6},{0,1,4,7},{2,3,4,5},{1,4,7,8},
            //S
            {4,5,6,7},{1,4,5,8},
            //T
            {3,4,5,7},{1,3,4,7},{1,3,4,5},{1,4,5,7},
            //Z
            {3,4,7,8},{2,4,5,7}
    }; //grid.spawnTetrimino(Tetriminos) depends on the indices being in numerical order

    Tetriminos(int size, int shapeIndex, int rotations){
        this.SIZE = size;
        this.SHAPE_INDEX = shapeIndex;
        this.ROTATIONS = rotations;
    }

    public static Tetriminos getRandom(){
        final int TETRIMINO_COUNT = 7;
        int index = (int) Math.floor(Math.random()*TETRIMINO_COUNT); // [0d-1d)
        return values()[index];
    }

    public int getROTATIONS(){
        return ROTATIONS;
    }

    public int getSIZE(){
        return SIZE;
    }

    public int[] getShape(int rotation){
        if (ROTATIONS == 0){
            return SHAPES[SHAPE_INDEX];
        }
        rotation %= ROTATIONS;
        return SHAPES[SHAPE_INDEX + rotation];
    }
}

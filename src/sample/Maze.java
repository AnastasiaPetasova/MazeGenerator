package sample;

import java.util.Arrays;

public class Maze {

    public static final boolean WALL = false;
    public static final boolean EMPTY = true;

    int width;
    int height;

    private boolean[][] field;

    Maze(int width, int height){
        this.width = width;
        this.height = height;
        this.field = new boolean[height][width];
    }

    Maze fill(boolean value) {
        for (boolean[] row : field) {
            Arrays.fill(row, value);
        }

        return this;
    }

    boolean get(int x, int y) {
        return field[y][x];
    }

    boolean isWall(int x, int y) {
        return WALL == get(x, y);
    }

    boolean isEmpty(int x, int y) {
        return EMPTY == get(x, y);
    }

    void set(int x, int y, boolean value) {
        field[y][x] = value;
    }

    void setWall(int x, int y) {
        set(x, y, WALL);
    }

    void setEmpty(int x, int y) {
        set(x, y, EMPTY);
    }
}

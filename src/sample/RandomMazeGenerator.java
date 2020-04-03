package sample;

import java.util.Random;

public class RandomMazeGenerator extends MazeGeneratorImpl {
    @Override
    protected void generate(int width, int height, Maze maze) {

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                maze.set(x, y, UtilsRandom.random.nextBoolean());
            }
        }

    }

}

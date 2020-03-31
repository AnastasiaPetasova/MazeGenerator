package sample;

import java.util.Random;

public class RandomMazeGenerator implements MazeGenerator {
    @Override
    public Maze generate(int width, int height) {

        Random random = new Random();

        Maze maze = new Maze(width, height);

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                maze.set(x, y, random.nextBoolean());
            }
        }

        return maze;

    }

}

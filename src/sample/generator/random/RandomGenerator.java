package sample.generator.random;

import sample.UtilsRandom;
import sample.generator.MazeGeneratorImpl;

public class RandomGenerator extends MazeGeneratorImpl {
    @Override
    protected void generate() {
        for (int z = 0; z < maze.layersCount; ++z) {
            for (int x = 0; x < maze.width; x++){
                for (int y = 0; y < maze.height; y++){
                    if (!maze.isOuterWall(x, y, z)) {
                        maze.set(x, y, z, UtilsRandom.nextBoolean());
                    }
                }
            }
        }

    }

}

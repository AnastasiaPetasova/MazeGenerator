package sample;

import java.awt.*;

import static sample.Maze.STEPS;

public class RandomWalkMazeGenerator extends MazeGeneratorImpl {
    @Override
    protected void generate(int width, int height, Maze maze) {

        maze.fill(Maze.WALL);

        double[] probabilities = { 0.4, 0.2, 0.2, 0.2 };

        double emptyCellsMinRatio = 0.4;
        int neededEmptyCellsCount = (int)(maze.width * maze.height * emptyCellsMinRatio);
        int emptyCellsCount = 0;

        boolean hasExit = false;
        for (int x = maze.startX, y = maze.startY, direction = 3; !hasExit || emptyCellsCount < neededEmptyCellsCount; ) {
            if (maze.isWall(x, y)) {
                maze.setEmpty(x, y);
                ++emptyCellsCount;
            }

            double probability = UtilsRandom.random.nextDouble();
            double sum = 0;
            for (int i = 0; i < probabilities.length; ++i) {
                sum += probabilities[i];
                if (probability <= sum) {
                    direction = (direction + i) % STEPS.length;
                    break;
                }
            }

            int nextX = x + STEPS[direction].x;
            int nextY = y + STEPS[direction].y;

            if (!maze.checkCell(nextX, nextY)) continue;

            if (maze.isWall(nextX, nextY)) {
                int emptyNeighborsCount = 0;
                for (Point step : STEPS) {
                    int neighbourX = nextX + step.x;
                    int neighbourY = nextY + step.y;

                    if (maze.checkCell(neighbourX, neighbourY)) {
                        if (Maze.EMPTY == maze.get(neighbourX, neighbourY)) ++emptyNeighborsCount;
                    }
                }

                if (emptyNeighborsCount > 1) continue;
            }

            x = nextX;
            y = nextY;

            if (maze.isEdge(x, y) && (x != maze.startX || y != maze.startY)) {
                hasExit = (x != 0 || y != 0);
            }
        }
    }


}

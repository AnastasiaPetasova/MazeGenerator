package sample.generator.random;

import sample.Maze;
import sample.generator.MazeGeneratorImpl;
import sample.UtilsRandom;

import java.awt.*;

import static sample.Maze.STEPS;

public class RandomWalkGenerator extends MazeGeneratorImpl {
    @Override
    protected void generate() {
        maze.fill(Maze.WALL);

        double[] probabilities = { 0.4, 0.2, 0.2, 0.2 };

        double emptyCellsMinRatio = 0.4;
        int neededEmptyCellsCount = (int)(maze.width * maze.height * emptyCellsMinRatio);
        int emptyCellsCount = 0;

        boolean hasExit = false;
        for (int x = maze.startX, y = maze.startY, direction = 3; !hasExit || emptyCellsCount < neededEmptyCellsCount ; ) {
            if (maze.isWall(x, y)) {
                maze.setEmpty(x, y);
                ++emptyCellsCount;
            }

            double probability = UtilsRandom.nextProbability();
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

            if (maze.isEdge(nextX, nextY)) {
                hasExit |= !maze.isStart(x, y);
            }

            if (maze.isOuterWall(nextX, nextY)) {
                continue;
            }

            if (maze.isWall(nextX, nextY)) {
                int emptyNeighborsCount = 0;
                for (Point step : STEPS) {
                    int neighbourX = nextX + step.x;
                    int neighbourY = nextY + step.y;

                    if (Maze.EMPTY == maze.get(neighbourX, neighbourY)) ++emptyNeighborsCount;
                }

                if (emptyNeighborsCount > 1) continue;
            }

            x = nextX;
            y = nextY;
        }
    }


}

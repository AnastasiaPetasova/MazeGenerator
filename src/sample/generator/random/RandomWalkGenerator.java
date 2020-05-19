package sample.generator.random;

import sample.Maze;
import sample.Point3D;
import sample.UtilsRandom;
import sample.generator.MazeGeneratorImpl;

import static sample.Maze.STEPS;

public class RandomWalkGenerator extends MazeGeneratorImpl {
    @Override
    protected void generate() {
        maze.fill(Maze.WALL);

        double goProbability = 0.4;
        double[] probabilities = { 0.2, 0.2, 0.2, 0.2, 0.2 };

        double emptyCellsMinRatio = 0.4;
        int neededEmptyCellsCount = (int)(maze.width * maze.height * emptyCellsMinRatio);
        int emptyCellsCount = 0;

        boolean hasExit = false;

        // negative are 0, 2, 4; positive are 1, 3, 5
        int direction = UtilsRandom.nextInt(0, 3) * 2 + 1;

        for (Point3D cur = new Point3D(maze.start);
             !hasExit || emptyCellsCount < neededEmptyCellsCount ; ) {

            if (maze.isWall(cur)) {
                maze.setEmpty(cur);
                ++emptyCellsCount;
            }

            if (goProbability < UtilsRandom.nextProbability()) {
                double probability = UtilsRandom.nextProbability();
                double sum = 0;
                for (int i = 0; i < probabilities.length; ++i) {
                    sum += probabilities[i];
                    if (probability <= sum) {
                        direction = (direction + i) % STEPS.length;
                        break;
                    }
                }
            }

            final int nextX = cur.x + STEPS[direction].x;
            final int nextY = cur.y + STEPS[direction].y;
            final int nextZ = cur.z + STEPS[direction].z;

            if (maze.isEdge(nextX, nextY, nextZ)) {
                hasExit |= !maze.isStart(cur);
            }

            if (maze.isOuterWall(nextX, nextY, nextZ)) {
                continue;
            }

            if (maze.isWall(nextX, nextY, nextZ)) {
                int emptyNeighborsCount = 0;
                for (Point3D step : STEPS) {
                    int neighbourX = nextX + step.x;
                    int neighbourY = nextY + step.y;
                    int neighbourZ = nextZ + step.z;

                    if (Maze.EMPTY == maze.get(neighbourX, neighbourY, neighbourZ))
                        ++emptyNeighborsCount;
                }

                if (emptyNeighborsCount > 1) continue;
            }

           cur.set(nextX, nextY, nextZ);
        }
    }


}

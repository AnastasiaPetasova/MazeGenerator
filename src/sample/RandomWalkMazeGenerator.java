package sample;

import java.util.*;
import java.awt.Point;

public class RandomWalkMazeGenerator implements MazeGenerator {
    @Override
    public Maze generate(int width, int height) {
        Random random = new Random();

        Maze maze = new Maze(width, height).fill(Maze.WALL);

        int startX = 0, startY = 0;

        double[] probabilities = { 0.4, 0.2, 0.2, 0.2  };
        int[][] steps = {
                { -1, 0 },
                { 0, -1 },
                { 1, 0 },
                { 0, 1 }
        };

        double emptyCellsMinRatio = 0.4;
        int neededEmptyCellsCount = (int)(maze.width * maze.height * emptyCellsMinRatio);
        int emptyCellsCount = 0;

        boolean hasExit = false;

        boolean[][] filledByMainAlgo = new boolean[maze.width][maze.height];
        for (int x = startX, y = startY, direction = 3; emptyCellsCount < neededEmptyCellsCount; ) {
            if (maze.isWall(x, y)) {
                maze.setEmpty(x, y);
                ++emptyCellsCount;
                filledByMainAlgo[x][y] = true;
            }

            double probability = random.nextDouble();
            double sum = 0;
            for (int i = 0; i < probabilities.length; ++i) {
                sum += probabilities[i];
                if (probability <= sum) {
                    direction = (direction + i) % steps.length;
                    break;
                }
            }

            int nextX = x + steps[direction][0];
            int nextY = y + steps[direction][1];

            if (!checkCell(nextX, maze.width, nextY, maze.height)) continue;

            if (maze.isWall(nextX, nextY)) {
                int emptyNeighborsCount = 0;
                for (int[] step : steps) {
                    int neighbourX = nextX + step[0];
                    int neighbourY = nextY + step[1];

                    if (checkCell(neighbourX, maze.width, neighbourY, maze.height)) {
                        if (Maze.EMPTY == maze.get(neighbourX, neighbourY)) ++emptyNeighborsCount;
                    }
                }

                if (emptyNeighborsCount > 1) continue;
            }

            x = nextX;
            y = nextY;

            if (x == 0 || x == maze.width - 1 || y == 0 || y == maze.height) {
                hasExit |= (x != 0 || y != 0);
            }
        }

        if (!hasExit) {
            int endX = random.nextInt(maze.width);
            int endY = random.nextInt(maze.height);

            if (random.nextBoolean()) {
                endX = (random.nextBoolean() ? 0 : maze.width - 1);
            } else {
                endY = (random.nextBoolean() ? 0 : maze.height - 1);
            }

            for (int x = endX, y = endY, direction = 1; ; ) {
                maze.setEmpty(x, y);

                double probability = random.nextDouble();
                double sum = 0;
                for (int i = 0; i < probabilities.length; ++i) {
                    sum += probabilities[i];
                    if (probability <= sum) {
                        direction = (direction + i) % steps.length;
                        break;
                    }
                }

                int nextX = x + steps[direction][0];
                int nextY = y + steps[direction][1];

                if (!checkCell(nextX, maze.width, nextY, maze.height)) continue;

                if (maze.isWall(nextX, nextY)) {
                    int emptyNeighborsCount = 0;
                    int mainAlgoNeighboursCount = 0;
                    for (int[] step : steps) {
                        int neighbourX = nextX + step[0];
                        int neighbourY = nextY + step[1];

                        if (checkCell(neighbourX, maze.width, neighbourY, maze.height)) {
                            if (Maze.EMPTY == maze.get(neighbourX, neighbourY)) {
                                ++emptyNeighborsCount;
                                if (filledByMainAlgo[neighbourX][neighbourY]) ++mainAlgoNeighboursCount;
                            }
                        }
                    }

                    if (mainAlgoNeighboursCount == 0 && emptyNeighborsCount > 1) continue;
                }

                x = nextX;
                y = nextY;

                if (filledByMainAlgo[x][y]) break;
            }
        }

        return maze;
    }

    static boolean checkIndex(int index, int size) {
        return 0 <= index && index < size;
    }

    static boolean checkCell(int x, int width, int y, int height) {
        return checkIndex(x, width) && checkIndex(y, height);
    }
}

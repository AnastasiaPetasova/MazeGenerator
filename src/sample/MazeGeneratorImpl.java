package sample;

import java.util.Random;

public abstract class MazeGeneratorImpl implements MazeGenerator {

    protected abstract void generate(int width, int height, Maze maze);

    @Override
    public Maze generate(int width, int height) {
        Maze maze = new Maze(width, height);

        int startX = 0, startY = 0;
        maze.setStart(startX, startY);

        generate(width, height, maze);

        findFinish(maze);

        return maze;
    }

    protected void findFinish(Maze maze) {
        Maze.ShortestPaths shortestPaths = maze.calculateShortestPaths();

        int finishX = maze.startX, finishY = maze.startY;

        for (int x = 0; x < maze.width; ++x) {
            for (int y = 0; y < maze.height; ++y) {
                if (!shortestPaths.isReachable(x, y)) continue;
                if (!maze.isEdge(x, y)) continue;
                if (maze.isStart(x, y)) continue;

                int distance = shortestPaths.getDistance(x, y);
                if (shortestPaths.getDistance(finishX, finishY) < distance) {
                    finishX = x;
                    finishY = y;
                }
            }
        }

        maze.setFinish(finishX, finishY);
    }

    @Override
    public String toString() {
        String fullClassName = getClass().getSimpleName();
        int generatorSuffixLength = "MazeGenerator".length();
        return fullClassName.substring(0, fullClassName.length() - generatorSuffixLength);
    }
}

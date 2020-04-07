package sample.generator;

import sample.Maze;
import sample.MazeGenerator;
import sample.UtilsRandom;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class MazeGeneratorImpl implements MazeGenerator {

    protected abstract void generate();

    protected Maze maze;

    @Override
    public Maze generate(int width, int height) {
        this.maze = new Maze(width + 2, height + 2);

        clear();

        preGenerate();

        generate();

        postGenerate();

        return maze;
    }

    private void coverByWalls() {
        for (int x = 0; x < maze.width; ++x) {
            maze.setWall(x, 0);
            maze.setWall(x, maze.height - 1);
        }

        for (int y = 0; y < maze.height; ++y) {
            maze.setWall(0, y);
            maze.setWall(maze.width - 1, y);
        }
    }

    private Point getRandomEdgeNeighbourCell(int x, int y) {
        List<Point> edgeNeighbours = new ArrayList<>();
        for (Point step : Maze.STEPS) {
            int neighbourX = x + step.x;
            int neighbourY = y + step.y;

            if (maze.isOuterWall(neighbourX, neighbourY)) {
                edgeNeighbours.add(new Point(neighbourX, neighbourY));
            }
        }

        return UtilsRandom.nextElement(edgeNeighbours);
    }

    protected void clear() {

    }

    protected void preGenerate() {
        coverByWalls();
        findStart();
    }

    protected void findStart() {
        maze.setStart(1, 1);
    }

    protected void postGenerate() {
        findFinish();

        Point startHole = getRandomEdgeNeighbourCell(maze.startX, maze.startY);
        maze.setEmpty(startHole.x, startHole.y);

        Point finishHole = getRandomEdgeNeighbourCell(maze.finishX, maze.finishY);
        maze.setEmpty(finishHole.x, finishHole.y);

        maze.setStart(startHole.x, startHole.y);
        maze.setFinish(finishHole.x, finishHole.y);
    }

    protected void findFinish() {
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
        int generatorSuffixLength = "Generator".length();
        return fullClassName.substring(0, fullClassName.length() - generatorSuffixLength);
    }
}

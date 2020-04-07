package sample.generator.perfect;

import sample.Maze;
import sample.generator.MazeGeneratorImpl;

import java.awt.Point;
import java.util.*;

public abstract class PerfectGenerator extends MazeGeneratorImpl {

    final boolean cutValue;
    final boolean filledValue;

    PerfectGenerator(boolean cutValue) {
        this.cutValue = cutValue;
        this.filledValue = !cutValue;
    }

    @Override
    protected void preGenerate() {
        super.preGenerate();
        maze.fill(filledValue);
    }

    abstract void fillStartedPoints(List<Point> startedPoints);

    List<Point> generateStartedPoints() {
        List<Point> startedPoints = new ArrayList<>();
        fillStartedPoints(startedPoints);
        return startedPoints;
    }

    List<Point> findNeighbours(int x, int y, boolean value) {
        List<Point> neighbours = new ArrayList<>();

        for (Point step : Maze.STEPS) {
            int neighbourX = x + step.x;
            int neighbourY = y + step.y;

            if (maze.notInside(neighbourX, neighbourY)) continue;
            if (value == maze.get(neighbourX, neighbourY)) {
                neighbours.add(new Point(neighbourX, neighbourY));
            }
        }

        return neighbours;
    }

    List<Point> findCutNeighbours(int x, int y) {
        return findNeighbours(x, y, cutValue);
    }

    List<Point> findFilledNeighbours(int x, int y) {
        return findNeighbours(x, y, filledValue);
    }

    boolean isStuckCell(int x, int y) {
        if (maze.notInside(x, y)) return false;
        if (maze.isOuterWall(x, y)) return false;

        List<Point> cutNeighbours = findCutNeighbours(x, y);
        return cutNeighbours.size() == 1; // 1 is (fromX, fromY)
    }

    void cutCell(Point p) {
        cutCell(p.x, p.y);
    }

    void cutCell(int x, int y) {
        maze.set(x, y, cutValue);
    }
}

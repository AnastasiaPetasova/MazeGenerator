package sample.generator.perfect;

import sample.Maze;

import java.awt.*;
import java.util.List;

public abstract class PerfectEmptyGenerator extends PerfectGenerator {

    public PerfectEmptyGenerator() {
        super(Maze.EMPTY);
    }

    void fillStartedPoints(List<Point> startedPoints) {
        startedPoints.add(new Point(maze.startX, maze.startY));
    }
}

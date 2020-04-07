package sample.generator.perfect;

import sample.Maze;

import java.awt.*;
import java.util.List;

public abstract class PerfectWallGenerator extends PerfectGenerator {

    public PerfectWallGenerator() {
        super(Maze.WALL);
    }

    void fillStartedPoints(List<Point> startedPoints) {
        for (int x = 0; x < maze.width; ++x) {
            for (int y = 0; y < maze.height; ++y) {
                if (maze.isOuterWall(x, y)) {
                    startedPoints.add(new Point(x, y));
                }
            }
        }
    }
}

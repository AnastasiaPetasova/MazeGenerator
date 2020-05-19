package sample.generator.perfect;

import sample.Maze;
import sample.Point3D;

import java.util.List;

public abstract class PerfectEmptyGenerator extends PerfectGenerator {

    public PerfectEmptyGenerator() {
        super(Maze.EMPTY);
    }

    void fillStartedPoints(List<Point3D> startedPoints) {
        startedPoints.add(new Point3D(maze.start));
    }
}

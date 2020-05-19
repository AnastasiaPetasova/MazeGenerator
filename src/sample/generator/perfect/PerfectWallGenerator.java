package sample.generator.perfect;

import sample.Maze;
import sample.Point3D;

import java.util.List;

public abstract class PerfectWallGenerator extends PerfectGenerator {

    public PerfectWallGenerator() {
        super(Maze.WALL);
    }

    void fillStartedPoints(List<Point3D> startedPoints) {
        for (int z = 0; z < maze.layersCount; ++z) {
            for (int x = 0; x < maze.width; ++x) {
                for (int y = 0; y < maze.height; ++y) {
                    if (maze.isOuterWall(x, y, z)) {
                        startedPoints.add(new Point3D(x, y, z));
                    }
                }
            }
        }
    }
}

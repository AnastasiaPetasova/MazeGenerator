package sample.generator.perfect;

import sample.Maze;
import sample.Point3D;
import sample.generator.MazeGeneratorImpl;

import java.util.ArrayList;
import java.util.List;

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

    abstract void fillStartedPoints(List<Point3D> startedPoints);

    List<Point3D> generateStartedPoints() {
        List<Point3D> startedPoints = new ArrayList<>();
        fillStartedPoints(startedPoints);
        return startedPoints;
    }

    List<Point3D> findNeighbours(int x, int y, int z, boolean value) {
        List<Point3D> neighbours = new ArrayList<>();

        for (Point3D step : Maze.STEPS) {
            int neighbourX = x + step.x;
            int neighbourY = y + step.y;
            int neighbourZ = z + step.z;

            if (maze.notInside(neighbourX, neighbourY, neighbourZ)) continue;
            if (value == maze.get(neighbourX, neighbourY, neighbourZ)) {
                neighbours.add(new Point3D(neighbourX, neighbourY, neighbourZ));
            }
        }

        return neighbours;
    }

    List<Point3D> findCutNeighbours(int x, int y, int z) {
        return findNeighbours(x, y, z, cutValue);
    }

    List<Point3D> findFilledNeighbours(Point3D point) {
        return findNeighbours(point.x, point.y, point.z, filledValue);
    }

    boolean isStuckCell(int x, int y, int z) {
        if (maze.notInside(x, y, z)) return false;
        if (maze.isOuterWall(x, y, z)) return false;

        List<Point3D> cutNeighbours = findCutNeighbours(x, y, z);
        return cutNeighbours.size() == 1; // 1 is (fromX, fromY)
    }

    void cutCell(Point3D p) {
        cutCell(p.x, p.y, p.z);
    }

    void cutCell(int x, int y, int z) {
        maze.set(x, y, z, cutValue);
    }
}

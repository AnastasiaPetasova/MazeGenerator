package sample.generator.perfect;

import sample.Point3D;

import java.util.ArrayList;
import java.util.List;

public abstract class PrimeGenerator extends PerfectEmptyGenerator {

    List<Point3D> activePoints;

    PrimeGenerator() {
        this.activePoints = new ArrayList<>();
    }

    @Override
    protected void clear() {
        activePoints.clear();
    }

    abstract Point3D calculateFrom();

    void onActionPointAdded(Point3D point) {

    }

    void addActiveNeighbours(Point3D point) {
        List<Point3D> filledNeighbours = findFilledNeighbours(point);
        for (Point3D filledNeighbour : filledNeighbours) {
            if (maze.isOuterWall(filledNeighbour)) continue;
            if (cutValue == maze.get(filledNeighbour)) continue;
            if (activePoints.contains(filledNeighbour)) continue;

            activePoints.add(filledNeighbour);
            onActionPointAdded(filledNeighbour);
        }
    }

    @Override
    protected void generate() {
        List<Point3D> startedPoints = generateStartedPoints();
        for (Point3D startedPoint : startedPoints) {
            cutCell(startedPoint);
        }

        this.activePoints = new ArrayList<>();
        for (Point3D startedPoint : startedPoints) {
            addActiveNeighbours(startedPoint);
        }

        while (activePoints.size() > 0) {
            Point3D from = calculateFrom();
            activePoints.remove(from);

            if (!isStuckCell(from.x, from.y, from.z)) continue;

            cutCell(from);
            addActiveNeighbours(from);
        }
    }
}

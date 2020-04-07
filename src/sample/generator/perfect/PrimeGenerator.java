package sample.generator.perfect;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class PrimeGenerator extends PerfectEmptyGenerator {

    List<Point> activePoints;

    PrimeGenerator() {
        this.activePoints = new ArrayList<>();
    }

    @Override
    protected void clear() {
        activePoints.clear();
    }

    abstract Point calculateFrom();

    void onActionPointAdded(Point point) {

    }

    void addActiveNeighbours(Point point) {
        List<Point> filledNeighbours = findFilledNeighbours(point.x, point.y);
        for (Point filledNeighbour : filledNeighbours) {
            if (maze.isOuterWall(filledNeighbour.x, filledNeighbour.y)) continue;
            if (cutValue == maze.get(filledNeighbour.x, filledNeighbour.y)) continue;
            if (activePoints.contains(filledNeighbour)) continue;

            activePoints.add(filledNeighbour);
            onActionPointAdded(filledNeighbour);
        }
    }

    @Override
    protected void generate() {
        List<Point> startedPoints = generateStartedPoints();
        for (Point startedPoint : startedPoints) {
            cutCell(startedPoint);
        }

        this.activePoints = new ArrayList<>();
        for (Point startedPoint : startedPoints) {
            addActiveNeighbours(startedPoint);
        }

        while (activePoints.size() > 0) {
            Point from = calculateFrom();
            activePoints.remove(from);

            if (!isStuckCell(from.x, from.y)) continue;

            cutCell(from);
            addActiveNeighbours(from);
        }
    }
}

package sample.generator.perfect;

import sample.Maze;
import sample.UtilsRandom;

import java.awt.*;
import java.util.List;

public class ReverseBacktrackingGenerator extends PerfectEmptyGenerator {

    @Override
    protected void generate() {
        class ReverseBacktrackingCutProcessor extends Maze.RandomDfsProcessor {

            @Override
            protected boolean onEnter(int fromX, int fromY) {
                cutCell(fromX, fromY);
                return false;
            }

            @Override
            protected boolean notGo(int toX, int toY) {
                return !isStuckCell(toX, toY);
            }
        }

        ReverseBacktrackingCutProcessor processor = new ReverseBacktrackingCutProcessor();

        List<Point> startedPoints = generateStartedPoints();

        Point startGeneratePoint = UtilsRandom.nextElement(startedPoints);
        processor.dfs(startGeneratePoint.x, startGeneratePoint.y);
    }
}

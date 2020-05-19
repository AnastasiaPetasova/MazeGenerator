package sample.generator.perfect;

import sample.Maze;
import sample.Point3D;
import sample.UtilsRandom;

import java.util.List;

public class ReverseBacktrackingGenerator extends PerfectEmptyGenerator {

    @Override
    protected void generate() {
        class ReverseBacktrackingCutProcessor extends Maze.RandomDfsProcessor {

            @Override
            protected boolean onEnter(int fromX, int fromY, int fromZ) {
                cutCell(fromX, fromY, fromZ);
                return false;
            }

            @Override
            protected boolean notGo(int toX, int toY, int toZ) {
                return !isStuckCell(toX, toY, toZ);
            }
        }

        ReverseBacktrackingCutProcessor processor = new ReverseBacktrackingCutProcessor();

        List<Point3D> startedPoints = generateStartedPoints();

        Point3D startGeneratePoint = UtilsRandom.nextElement(startedPoints);
        processor.dfs(startGeneratePoint.x, startGeneratePoint.y, startGeneratePoint.z);
    }
}

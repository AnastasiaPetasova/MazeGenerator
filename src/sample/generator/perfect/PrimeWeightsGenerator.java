package sample.generator.perfect;

import sample.Point3D;
import sample.UtilsRandom;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class PrimeWeightsGenerator extends PrimeGenerator {

    int[][][] weights;
    Queue<Point3D> priorityQueue;

    @Override
    protected void preGenerate() {
        super.preGenerate();

        this.weights = new int[maze.width][maze.height][maze.layersCount];
        for (int x = 0; x < maze.width; ++x) {
            for (int y = 0; y < maze.height; ++y) {
                for (int z = 0; z < maze.layersCount; ++z) {
                    weights[x][y][z] = UtilsRandom.nextInt(
                            0, maze.width * maze.height * maze.layersCount
                    );
                }
            }
        }

        this.priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(a -> weights[a.x][a.y][a.z])
        );
    }

    @Override
    void onActionPointAdded(Point3D point) {
        super.onActionPointAdded(point);
        priorityQueue.add(point);
    }

    Point3D calculateFrom() {
        return priorityQueue.poll();
    }
}

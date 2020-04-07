package sample.generator.perfect;

import sample.UtilsRandom;

import java.awt.Point;
import java.util.*;

public class PrimeWeightsGenerator extends PrimeGenerator {

    int[][] weights;
    Queue<Point> priorityQueue;

    @Override
    protected void preGenerate() {
        super.preGenerate();

        this.weights = new int[maze.width][maze.height];
        for (int x = 0; x < maze.width; ++x) {
            for (int y = 0; y < maze.height; ++y) {
                weights[x][y] = UtilsRandom.nextInt(0, maze.width * maze.height);
            }
        }

        this.priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(a -> weights[a.x][a.y])
        );
    }

    @Override
    void onActionPointAdded(Point point) {
        super.onActionPointAdded(point);
        priorityQueue.add(point);
    }

    Point calculateFrom() {
        return priorityQueue.poll();
    }
}

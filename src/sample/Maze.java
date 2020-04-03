package sample;

import java.awt.Point;
import java.util.*;

public class Maze {

    static final Point[] STEPS = {
            new Point(-1, 0),
            new Point(0, -1),
            new Point(1, 0),
            new Point(0, 1)
    };

    static boolean checkIndex(int index, int size) {
        return 0 <= index && index < size;
    }

    static boolean checkCell(int x, int width, int y, int height) {
        return checkIndex(x, width) && checkIndex(y, height);
    }

    static boolean isEdgeIndex(int index, int size) {
        return 0 == index || size - 1 == index;
    }

    static final char WALL_CHAR = '#';
    private static final char EMPTY_CHAR = ' ';

    public static final boolean WALL = false;
    public static final boolean EMPTY = true;

    int width;
    int height;

    private boolean[][] field;

    int startX, startY;
    int finishX, finishY;

    Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.field = new boolean[height][width];
    }

    void setStart(int x, int y) {
        this.startX = x;
        this.startY = y;
    }

    void setFinish(int x, int y) {
        this.finishX = x;
        this.finishY = y;
    }

    Maze fill(boolean value) {
        for (boolean[] row : field) {
            Arrays.fill(row, value);
        }

        return this;
    }

    boolean get(int x, int y) {
        return field[y][x];
    }

    boolean isWall(int x, int y) {
        return WALL == get(x, y);
    }

    boolean isEmpty(int x, int y) {
        return EMPTY == get(x, y);
    }

    boolean isEdge(int x, int y) {
        return isEdgeIndex(x, width) || isEdgeIndex(y, height);
    }

    boolean isStart(int x, int y) {
        return startX == x && startY == y;
    }

    boolean isFinish(int x, int y) {
        return finishX == x && finishY == y;
    }


    void set(int x, int y, boolean value) {
        field[y][x] = value;
    }

    void setWall(int x, int y) {
        set(x, y, WALL);
    }

    void setEmpty(int x, int y) {
        set(x, y, EMPTY);
    }

    boolean checkCell(int x, int y) {
        return checkCell(x, width, y, height);
    }

    static class ShortestPaths {

        public static final Point NO_PARENT = new Point(-1, -1);
        public static final int UNREACHABLE = -1;
        public static final List<Point> NO_PATH = new ArrayList<>();

        Point start;
        private int[][] distances;
        private Point[][] parents;

        public ShortestPaths(Point start, int[][] distances, Point[][] parents) {
            this.start = start;
            this.distances = distances;
            this.parents = parents;
        }

        boolean isReachable(int x, int y) {
            return UNREACHABLE != distances[x][y];
        }

        int getDistance(int x, int y) {
            return distances[x][y];
        }

        List<Point> calculateShortestPathTo(int x, int y) {
            if (!isReachable(x, y)) return NO_PATH;

            List<Point> path = new ArrayList<>();

            for (Point cur = new Point(x, y),
                 startParent = parents[start.x][start.y]; !cur.equals(startParent); ) {
                path.add(cur);
                cur = parents[cur.x][cur.y];
            }

            Collections.reverse(path);
            return path;
        }
    }

    private ShortestPaths shortestPaths;

    ShortestPaths calculateShortestPaths() {
        Point start = new Point(startX, startY);
        if (null != shortestPaths && start.equals(shortestPaths.start)) {
            return shortestPaths;
        }

        int[][] distances = new int[width][height];
        for (int[] d1 : distances) {
            Arrays.fill(d1, ShortestPaths.UNREACHABLE);
        }

        Point[][] parents = new Point[width][height];

        Queue<Point> queue = new ArrayDeque<>();

        queue.add(start);
        distances[start.x][start.y] = 0;
        parents[start.x][start.y] = ShortestPaths.NO_PARENT;

        while (queue.size() > 0) {
            Point from = queue.poll();

            for (Point step : STEPS) {
                int toX = from.x + step.x;
                int toY = from.y + step.y;

                if (!checkCell(toX, toY)) continue;
                if (isWall(toX, toY)) continue;

                if (distances[toX][toY] != ShortestPaths.UNREACHABLE) continue;

                distances[toX][toY] = distances[from.x][from.y] + 1;
                parents[toX][toY] = from;

                queue.add(new Point(toX, toY));
            }
        }

        return shortestPaths = new ShortestPaths(start, distances, parents);
    }

    class RandomDfsProcessor {

        boolean[][] used;
        boolean foundFinish;

        public RandomDfsProcessor() {
            this.used = new boolean[width][height];
            this.foundFinish = false;
        }

        int[] calculatePermutation(int permutationIndex) {
            boolean[] usedIndexes = new boolean[4];

            int[] facts = new int[4];
            facts[0] = 1;
            for (int i = 1; i < 4; ++i) facts[i] = i * facts[i - 1];

            int[] permutation = new int[4];
            for (int i = 3; i >= 0; --i) {
                for (int index = 0; index < 4; ++index) {
                    if (usedIndexes[index]) continue;
                    if (permutationIndex >= facts[i]) {
                        permutationIndex -= facts[i];
                    } else {
                        permutation[i] = index;
                        usedIndexes[index] = true;
                        break;
                    }
                }
            }

            return permutation;
        }

        int dfs(int fromX, int fromY) {
            used[fromX][fromY] = true;

            int length = 1;
            if (isFinish(fromX, fromY)) {
                foundFinish = true;
                return length;
            }

            int permutationIndex = UtilsRandom.random.nextInt(24);
            int[] stepPermutation = calculatePermutation(permutationIndex);

            for (int stepIndex : stepPermutation) {
                Point step = STEPS[stepIndex];

                int toX = fromX + step.x;
                int toY = fromY + step.y;

                if (!checkCell(toX, toY)) continue;
                if (isWall(toX, toY)) continue;
                if (used[toX][toY]) continue;

                length += dfs(toX, toY);
                if (foundFinish) return length;
            }

            return length;
        }
    }

    public int calculateRandomPathLength() {
        RandomDfsProcessor processor  = new RandomDfsProcessor();

        return processor.dfs(startX, startY);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(width).append(' ').append(height).append('\n');
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                stringBuilder.append(isWall(x, y) ? WALL_CHAR : EMPTY_CHAR);
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}

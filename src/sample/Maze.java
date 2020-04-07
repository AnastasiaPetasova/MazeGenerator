package sample;

import java.awt.Point;
import java.util.*;

public class Maze {

    public static final Point[] STEPS = {
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

    static boolean isNearEdgeIndex(int index, int size) {
        return 1 == index || size - 2 == index;
    }

    static boolean isEdgeIndex(int index, int size) {
        return 0 == index || size - 1 == index;
    }

    static final char WALL_CHAR = '#';
    private static final char EMPTY_CHAR = ' ';

    public static final boolean WALL = false;
    public static final boolean EMPTY = true;

    public int width;
    public int height;

    private boolean[][] field;

    public int startX;
    public int startY;
    public int finishX;
    public int finishY;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.field = new boolean[height][width];
    }

    public void setStart(int x, int y) {
        this.startX = x;
        this.startY = y;
    }

    public void setFinish(int x, int y) {
        this.finishX = x;
        this.finishY = y;
    }

    public Maze fill(boolean value) {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (!isOuterWall(x, y)) {
                    set(x, y, value);
                }
            }
        }

        return this;
    }

    public boolean get(int x, int y) {
        return field[y][x];
    }

    public boolean isWall(int x, int y) {
        return WALL == get(x, y);
    }

    boolean isEmpty(int x, int y) {
        return EMPTY == get(x, y);
    }

    public boolean isEdge(int x, int y) {
        return isNearEdgeIndex(x, width) || isNearEdgeIndex(y, height);
    }

    public boolean isOuterWall(int x, int y) {
        return isEdgeIndex(x, width) || isEdgeIndex(y, height);
    }

    public boolean isStart(int x, int y) {
        return startX == x && startY == y;
    }

    boolean isFinish(int x, int y) {
        return finishX == x && finishY == y;
    }

    public void set(int x, int y, boolean value) {
        field[y][x] = value;
    }

    public void setWall(int x, int y) {
        set(x, y, WALL);
    }

    public void setEmpty(int x, int y) {
        set(x, y, EMPTY);
    }

    public boolean notInside(int x, int y) {
        return !checkCell(x, width, y, height);
    }

    public static class ShortestPaths {

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

        public boolean isReachable(int x, int y) {
            return UNREACHABLE != distances[x][y];
        }

        public int getDistance(int x, int y) {
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

    public ShortestPaths calculateShortestPaths() {
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

                if (notInside(toX, toY)) continue;
                if (isWall(toX, toY)) continue;

                if (distances[toX][toY] != ShortestPaths.UNREACHABLE) continue;

                distances[toX][toY] = distances[from.x][from.y] + 1;
                parents[toX][toY] = from;

                queue.add(new Point(toX, toY));
            }
        }

        return shortestPaths = new ShortestPaths(start, distances, parents);
    }

    public static class RandomDfsProcessor {

        static int[] facts;

        static {
            facts = new int[STEPS.length + 1];
            facts[0] = 1;
            for (int i = 1; i < facts.length; ++i) {
                facts[i] = facts[i - 1] * i;
            }
        }

        static int[] calculatePermutation(int permutationIndex) {
            boolean[] usedIndexes = new boolean[STEPS.length];

            int[] permutation = new int[STEPS.length];
            for (int i = 3; i >= 0; --i) {
                for (int index = 0; index < STEPS.length; ++index) {
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

        static int[] generateRandomStepsPermutation() {
            int permutationIndex = UtilsRandom.nextInt(0, facts[STEPS.length]);
            return calculatePermutation(permutationIndex);
        }

        protected boolean onEnter(int fromX, int fromY) {
            return false;
        }

        protected void onExit(int fromX, int fromY) {

        }

        protected boolean notGo(int toX, int toY) {
            return false;
        }

        protected boolean afterToDfs(int toX, int toY) {
            return false;
        }

        public void dfs(int fromX, int fromY) {
            if (onEnter(fromX, fromY)) {
                return;
            }

            int[] stepPermutation = generateRandomStepsPermutation();
            for (int stepIndex : stepPermutation) {
                Point step = STEPS[stepIndex];

                int toX = fromX + step.x;
                int toY = fromY + step.y;

                if (notGo(toX, toY)) {
                    continue;
                }

                dfs(toX, toY);

                if (afterToDfs(toX, toY)) {
                    return;
                }
            }

            onExit(fromX, fromY);
        }
    }

    public int calculateRandomPathLength() {
        class RandomPathLengthCalculator extends RandomDfsProcessor {

            boolean[][] used;
            boolean foundFinish;
            int totalLength;

            public RandomPathLengthCalculator() {
                this.used = new boolean[width][height];
                this.foundFinish = false;
            }

            @Override
            protected boolean onEnter(int fromX, int fromY) {
                used[fromX][fromY] = true;

                ++totalLength;
                if (isFinish(fromX, fromY)) {
                    foundFinish = true;
                }

                return foundFinish;
            }

            @Override
            protected boolean notGo(int toX, int toY) {
                if (notInside(toX, toY)) return true;
                if (isWall(toX, toY)) return true;
                if (used[toX][toY]) return true;

                return false;
            }

            @Override
            protected boolean afterToDfs(int toX, int toY) {
                return foundFinish;
            }
        }

        RandomPathLengthCalculator processor  = new RandomPathLengthCalculator();

        processor.dfs(startX, startY);

        return processor.totalLength;
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

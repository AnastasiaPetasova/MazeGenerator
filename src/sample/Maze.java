package sample;

import java.util.*;

public class Maze {

    public static final Point3D[] STEPS;

    static {
        List<Point3D> directions = new ArrayList<>();
        int[] deltas = { -1, 1 };

        for (int dx : deltas) {
            directions.add(new Point3D(dx, 0, 0));
        }

        for (int dy : deltas) {
            directions.add(new Point3D(0, dy, 0));
        }

        for (int dz : deltas) {
            directions.add(new Point3D(0, 0, dz));
        }

        STEPS = directions.toArray(new Point3D[0]);
    }

    static boolean checkIndex(int index, int size) {
        return 0 <= index && index < size;
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
    public int layersCount;

    private boolean[][][] field;

    public Point3D start;
    public Point3D finish;

    public Maze(int width, int height, int layersCount) {
        this.width = width;
        this.height = height;
        this.layersCount = layersCount;
        this.field = new boolean[layersCount][height][width];
    }

    public void setStart(Point3D point) {
        setStart(point.x, point.y, point.z);
    }

    public void setStart(int x, int y, int z) {
        this.start = new Point3D(x, y, z);
    }

    public void setFinish(Point3D point) {
        setFinish(point.x, point.y, point.z);
    }

    public void setFinish(int x, int y, int z) {
        this.finish = new Point3D(x, y, z);
    }

    public void fill(boolean value) {
        for (int z = 0; z < layersCount; ++z) {
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    if (!isOuterWall(x, y, z)) {
                        set(x, y, z, value);
                    }
                }
            }
        }
    }

    public boolean get(Point3D point) {
        return get(point.x, point.y, point.z);
    }

    public boolean get(int x, int y, int z) {
        return field[z][y][x];
    }

    public boolean isWall(Point3D point) {
        return isWall(point.x, point.y, point.z);
    }

    public boolean isWall(int x, int y, int z) {
        return WALL == get(x, y, z);
    }

    boolean isEmpty(int x, int y, int z) {
        return EMPTY == get(x, y, z);
    }

    public boolean isEdge(int x, int y, int z) {
        return isNearEdgeIndex(x, width) || isNearEdgeIndex(y, height);// && !isNearEdgeIndex(z, layersCount);
    }

    public boolean isOuterWall(Point3D point) {
        return isOuterWall(point.x, point.y, point.z);
    }

    public boolean isOuterWall(int x, int y, int z) {
        return isEdgeIndex(x, width) || isEdgeIndex(y, height) || isEdgeIndex(z, layersCount);
    }

    public boolean canBeExit(int x, int y, int z) {
        return !isEdgeIndex(z, layersCount) && (isEdgeIndex(x, width) || isEdgeIndex(y, height));
    }

    public boolean isStart(Point3D point) {
        return isStart(point.x, point.y, point.z);
    }

    public boolean isStart(int x, int y, int z) {
        return null != start && start.equalsTo(x, y, z);
    }

    boolean isFinish(int x, int y, int z) {
        return null != finish && finish.equalsTo(x, y, z);
    }

    public void set(int x, int y, int z, boolean value) {
        field[z][y][x] = value;
    }

    public void setWall(int x, int y, int z) {
        set(x, y, z, WALL);
    }

    public void setEmpty(Point3D point) {
        setEmpty(point.x, point.y, point.z);
    }

    public void setEmpty(int x, int y, int z) {
        set(x, y, z, EMPTY);
    }

    public boolean checkCell(int x, int y, int z) {
        return checkIndex(x, width) && checkIndex(y, height) && checkIndex(z, layersCount);
    }

    public boolean notInside(int x, int y, int z) {
        return !checkCell(x, y, z);
    }

    public static class ShortestPaths {

        public static final Point3D NO_PARENT = new Point3D(-1, -1, -1);
        public static final int UNREACHABLE = -1;
        public static final List<Point3D> NO_PATH = new ArrayList<>();

        Point3D start;
        private int[][][] distances;
        private Point3D[][][] parents;

        public ShortestPaths(Point3D start, int[][][] distances, Point3D[][][] parents) {
            this.start = start;
            this.distances = distances;
            this.parents = parents;
        }

        public boolean isReachable(int x, int y, int z) {
            return UNREACHABLE != distances[x][y][z];
        }

        public int getDistance(Point3D point) {
            return getDistance(point.x, point.y, point.z);
        }

        public int getDistance(int x, int y, int z) {
            return distances[x][y][z];
        }

        List<Point3D> calculateShortestPathTo(Point3D target) {

            if (!isReachable(target.x, target.y, target.z)) return NO_PATH;

            List<Point3D> path = new ArrayList<>();

            for (Point3D cur = new Point3D(target),
                 startParent = parents[start.x][start.y][start.z];
                 !cur.equals(startParent); ) {
                path.add(cur);
                cur = parents[cur.x][cur.y][cur.z];
            }

            Collections.reverse(path);
            return path;
        }
    }

    private ShortestPaths shortestPaths;

    public ShortestPaths calculateShortestPaths() {
        if (null != shortestPaths && start.equals(shortestPaths.start)) {
            return shortestPaths;
        }

        int[][][] distances = new int[width][height][layersCount];
        for (int[][] d2 : distances) {
            for (int[] d1 : d2) {
                Arrays.fill(d1, ShortestPaths.UNREACHABLE);
            }
        }

        Point3D[][][] parents = new Point3D[width][height][layersCount];

        Queue<Point3D> queue = new ArrayDeque<>();

        queue.add(start);
        distances[start.x][start.y][start.z] = 0;
        parents[start.x][start.y][start.z] = ShortestPaths.NO_PARENT;

        while (queue.size() > 0) {
            Point3D from = queue.poll();

            for (Point3D step : STEPS) {
                int toX = from.x + step.x;
                int toY = from.y + step.y;
                int toZ = from.z + step.z;

                if (notInside(toX, toY, toZ)) continue;
                if (isWall(toX, toY, toZ)) continue;

                if (distances[toX][toY][toZ] != ShortestPaths.UNREACHABLE) continue;

                distances[toX][toY][toZ] = distances[from.x][from.y][from.z] + 1;
                parents[toX][toY][toZ] = from;

                queue.add(new Point3D(toX, toY, toZ));
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
            for (int i = STEPS.length - 1; i >= 0; --i) {
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

        protected boolean onEnter(int fromX, int fromY, int fromZ) {
            return false;
        }

        protected void onExit(int fromX, int fromY, int fromZ) {

        }

        protected boolean notGo(int toX, int toY, int toZ) {
            return false;
        }

        protected boolean afterToDfs(int toX, int toY, int toZ) {
            return false;
        }

        public void dfs(int fromX, int fromY, int fromZ) {
            if (onEnter(fromX, fromY, fromZ)) {
                return;
            }

            int[] stepPermutation = generateRandomStepsPermutation();
            for (int stepIndex : stepPermutation) {
                Point3D step = STEPS[stepIndex];

                int toX = fromX + step.x;
                int toY = fromY + step.y;
                int toZ = fromZ + step.z;

                if (notGo(toX, toY, toZ)) {
                    continue;
                }

                dfs(toX, toY, toZ);

                if (afterToDfs(toX, toY, toZ)) {
                    return;
                }
            }

            onExit(fromX, fromY, fromZ);
        }
    }

    public int calculateRandomPathLength() {
        class RandomPathLengthCalculator extends RandomDfsProcessor {

            boolean[][][] used;
            boolean foundFinish;
            int totalLength;

            public RandomPathLengthCalculator() {
                this.used = new boolean[width][height][layersCount];
                this.foundFinish = false;
            }

            @Override
            protected boolean onEnter(int fromX, int fromY, int fromZ) {
                used[fromX][fromY][fromZ] = true;

                ++totalLength;
                if (isFinish(fromX, fromY, fromZ)) {
                    foundFinish = true;
                }

                return foundFinish;
            }

            @Override
            protected boolean notGo(int toX, int toY, int toZ) {
                if (notInside(toX, toY, toZ)) return true;
                if (isWall(toX, toY, toZ)) return true;
                return used[toX][toY][toZ];
            }

            @Override
            protected boolean afterToDfs(int toX, int toY, int toZ) {
                return foundFinish;
            }
        }

        RandomPathLengthCalculator processor  = new RandomPathLengthCalculator();

        processor.dfs(start.x, start.y, start.z);

        return processor.totalLength;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(layersCount).append(' ')
                .append(width).append(' ')
                .append(height).append('\n');
        for (int z = 0; z < layersCount; ++z) {
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    stringBuilder.append(isWall(x, y, z) ? WALL_CHAR : EMPTY_CHAR);
                }
                stringBuilder.append('\n');
            }
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
}

package sample.generator;

import sample.Maze;
import sample.MazeGenerator;
import sample.Point3D;
import sample.UtilsRandom;

import java.util.ArrayList;
import java.util.List;

public abstract class MazeGeneratorImpl implements MazeGenerator {

    protected abstract void generate();

    protected Maze maze;

    @Override
    public Maze generate(int width, int height, int layersCount) {
        this.maze = new Maze(width + 2, height + 2, layersCount + 2);

        clear();

        preGenerate();

        generate();

        postGenerate();

        return maze;
    }

    private void coverByWalls() {
        for (int z = 0; z < maze.layersCount; ++z) {
            for (int x = 0; x < maze.width; ++x) {
                maze.setWall(x, 0, z);
                maze.setWall(x, maze.height - 1, z);
            }

            for (int y = 0; y < maze.height; ++y) {
                maze.setWall(0, y, z);
                maze.setWall(maze.width - 1, y, z);
            }
        }

        for (int x = 0; x < maze.width; ++x) {
            for (int y = 0; y < maze.height; ++y) {
                maze.setWall(x, y, 0);
                maze.setWall(x, y, maze.layersCount - 1);
            }
        }
    }

    private Point3D getRandomEdgeNeighbourCell(Point3D point) {
        List<Point3D> edgeNeighbours = new ArrayList<>();
        for (Point3D step : Maze.STEPS) {
            int neighbourX = point.x + step.x;
            int neighbourY = point.y + step.y;
            int neighbourZ = point.z + step.z;

            if (maze.isOuterWall(neighbourX, neighbourY, neighbourZ)) {
                edgeNeighbours.add(new Point3D(neighbourX, neighbourY, neighbourZ));
            }
        }

        return UtilsRandom.nextElement(edgeNeighbours);
    }

    protected void clear() {

    }

    protected void preGenerate() {
        coverByWalls();
        findStart();
    }

    protected void findStart() {
        maze.setStart(1, 1, 1);
    }

    protected void postGenerate() {
        findFinish();

        Point3D startHole = getRandomEdgeNeighbourCell(maze.start);
        maze.setEmpty(startHole);

        Point3D finishHole = getRandomEdgeNeighbourCell(maze.finish);
        maze.setEmpty(finishHole);

        maze.setStart(startHole);
        maze.setFinish(finishHole);
    }

    protected void findFinish() {
        Maze.ShortestPaths shortestPaths = maze.calculateShortestPaths();

        Point3D finish = new Point3D(maze.start);

        for (int z = 0; z < maze.layersCount; ++z) {
            for (int x = 0; x < maze.width; ++x) {
                for (int y = 0; y < maze.height; ++y) {
                    if (!shortestPaths.isReachable(x, y, z)) continue;
                    if (!maze.isEdge(x, y, z)) continue;
                    if (maze.isStart(x, y, z)) continue;

                    int distance = shortestPaths.getDistance(x, y, z);
                    if (shortestPaths.getDistance(finish) < distance) {
                        finish.set(x, y, z);
                    }
                }
            }
        }


        maze.setFinish(finish);
    }

    @Override
    public String toString() {
        String fullClassName = getClass().getSimpleName();
        int generatorSuffixLength = "Generator".length();
        return fullClassName.substring(0, fullClassName.length() - generatorSuffixLength);
    }
}

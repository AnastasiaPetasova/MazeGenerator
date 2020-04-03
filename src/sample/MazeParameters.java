package sample;

public class MazeParameters {
    Maze maze;
    MazeGenerator mazeGenerator;
    int minPathLength;
    int numberOfAccessibleCells;
    double averagePathLength; //middle

    public MazeParameters(Maze maze, MazeGenerator mazeGenerator) {
        this.maze = maze;
        this.mazeGenerator = mazeGenerator;
        this.minPathLength = -1;
        this.numberOfAccessibleCells = -1;
        this.averagePathLength = -1;
    }

    public void calculateParameters() {
        Maze.ShortestPaths shortestPaths = maze.calculateShortestPaths();

        this.numberOfAccessibleCells = 0;
        for (int x = 0; x < maze.width; ++x) {
            for (int y = 0; y < maze.height; ++y) {
                if (shortestPaths.isReachable(x, y)) {
                    numberOfAccessibleCells++;
                }
            }
        }

        this.minPathLength = shortestPaths.getDistance(maze.finishX, maze.finishY);

        this.averagePathLength = 0;

        int iterationsCount = 100; // метод Монте-Карло
        for (int iteration = 0; iteration < iterationsCount; ++iteration) {
            int randomPathLength = maze.calculateRandomPathLength();
            averagePathLength += randomPathLength;
        }

        averagePathLength /= iterationsCount;
    }

    public String toParametersString() {
        return String.format("Минимальная длина пути = %d; количество достижимых клеток = %d; средняя длина пути = %.2f", minPathLength, numberOfAccessibleCells, averagePathLength);
    }

    @Override
    public String toString() {
        return  "" +
                minPathLength + '\n' +
                numberOfAccessibleCells + '\n' +
                averagePathLength + '\n';
    }
}

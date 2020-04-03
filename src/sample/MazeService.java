package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.StringTokenizer;

public class MazeService {


    public static final String TXT = "txt", PNG = "png";
    public static final String DIRECTORY = "mazes/", TEXT_DIRECTORY = DIRECTORY + "text/", IMAGE_DIRECTORY = DIRECTORY + "image/";

    public static String createTimeStampFileName(String extension) {
        return ("" + new Date()).replace(":", "-") + "." + extension;
    }

    public static void saveToText(String fileName, MazeParameters mazeParameters) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.println(mazeParameters.maze);
            writer.println();
            writer.println(mazeParameters.mazeGenerator);
            writer.println();
            writer.println(mazeParameters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MazeParameters loadFromText(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Maze maze = readMaze(reader);
            reader.readLine();
            MazeGenerator generator = readGenerator(reader);
            reader.readLine();

            MazeParameters parameters = new MazeParameters(maze, generator);
            readParameters(reader, parameters);
            return parameters;
        } catch (IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Maze readMaze(BufferedReader reader) throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
        int width = Integer.parseInt(tokenizer.nextToken());
        int height = Integer.parseInt(tokenizer.nextToken());

        Maze maze = new Maze(width, height);
        for (int y = 0; y < maze.height; ++y) {
            String row = reader.readLine();
            for (int x = 0; x < maze.width; ++x) {
                if (Maze.WALL_CHAR == row.charAt(x)) {
                    maze.setWall(x, y);
                } else {
                    maze.setEmpty(x, y);
                }
            }
        }

        return maze;
    }

    private static MazeGenerator readGenerator(BufferedReader reader) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String namePrefix = reader.readLine();
        String fullClassName = "sample." + namePrefix + "MazeGenerator";
        return (MazeGenerator) Class.forName(fullClassName).getConstructor().newInstance();
    }

    private static void readParameters(BufferedReader reader, MazeParameters parameters) {
        try {
            parameters.minPathLength = Integer.parseInt(reader.readLine());
            parameters.numberOfAccessibleCells = Integer.parseInt(reader.readLine());
            parameters.averagePathLength = Double.parseDouble(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveAsImage(String fileName, Canvas canvas) {
        WritableImage image = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, image);

        File file = new File(fileName);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

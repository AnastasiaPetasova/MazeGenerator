package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;

public class MazeService {

    private static final char WALL_CHAR = '#', EMPTY_CHAR = ' ';
    public static final String TXT = "txt", PNG = "png";
    public static final String DIRECTORY = "mazes/", TEXT_DIRECTORY = DIRECTORY + "text/", IMAGE_DIRECTORY = DIRECTORY + "image/";

    public static String createTimeStampFileName(String extension) {
        return ("" + new Date()).replace(":", "-") + "." + extension;
    }

    public static void saveToText(String fileName, Maze maze) {

        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.print(maze.width);
            writer.print(" ");
            writer.print(maze.height);
            writer.println();
            for (int y = 0; y < maze.height; ++y) {
                for (int x = 0; x < maze.width; ++x) {
                    writer.print(maze.isWall(x, y) ? WALL_CHAR : EMPTY_CHAR);
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Maze loadFromText(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringTokenizer tokenizer = new StringTokenizer(reader.readLine());
            int width = Integer.parseInt(tokenizer.nextToken());
            int height = Integer.parseInt(tokenizer.nextToken());

            Maze maze = new Maze(width, height);
            for (int y = 0; y < maze.height; ++y) {
                String row = reader.readLine();
                for (int x = 0; x < maze.width; ++x) {
                    if (WALL_CHAR == row.charAt(x)) {
                        maze.setWall(x, y);
                    } else {
                        maze.setEmpty(x, y);
                    }
                }
            }

            return maze;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

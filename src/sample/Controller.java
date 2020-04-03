
package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Controller implements Initializable {

    @FXML
    ListView<String> mazeType;

    @FXML
    Label mazeParametersLabel;

    @FXML
    Canvas canvas;

    @FXML
    Button generateButton;

    @FXML
    Button saveToTextButton;

    @FXML
    Button loadFromTextButton;

    @FXML
    Button saveAsImageButton;

    private Point getRandomEdgeNeighbourCell(Maze maze, int x, int y) {
        List<Point> edgeNeighbours = new ArrayList<>();
        for (Point step : Maze.STEPS) {
            int neighbourX = x + step.x;
            int neighbourY = y + step.y;

            if (!maze.checkCell(neighbourX, neighbourY)) {
                edgeNeighbours.add(new Point(neighbourX, neighbourY));
            }
        }

        Random random = new Random();
        return edgeNeighbours.get(random.nextInt(edgeNeighbours.size()));
    }

    private void drawMazeCell(GraphicsContext graphicsContext, double cellWidth, double cellHeight, int x, int y) {
        graphicsContext.fillRect((1 + x) * cellWidth, (1 + y) * cellHeight, cellWidth, cellHeight);
    }

    private void drawMaze(Maze maze){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double cellWidth = canvas.getWidth() / (maze.width + 2);
        double cellHeight = canvas.getHeight() / (maze.height + 2);

        graphicsContext.setFill(Color.WHITE);

        for (int x = 0; x < maze.width; x++){
            for (int y = 0; y < maze.height; y++){
                if (maze.isEmpty(x, y)){
                    drawMazeCell(graphicsContext, cellWidth, cellHeight, x, y);
                }
            }
        }

        graphicsContext.setFill(Color.RED);

        Point startHole = getRandomEdgeNeighbourCell(maze, maze.startX, maze.startY);
        drawMazeCell(graphicsContext, cellWidth, cellHeight, startHole.x, startHole.y);

        Point finishHole = getRandomEdgeNeighbourCell(maze, maze.finishX, maze.finishY);
        drawMazeCell(graphicsContext, cellWidth, cellHeight, finishHole.x, finishHole.y);

        graphicsContext.setFill(Color.YELLOW);

        List<Point> startFinishPath = maze.calculateShortestPaths().calculateShortestPathTo(maze.finishX, maze.finishY);
        for (Point cell : startFinishPath) {
            drawMazeCell(graphicsContext, cellWidth, cellHeight, cell.x, cell.y);
        }
    }

    MazeGenerator lastMazeGenerator;
    Maze lastMaze;
    MazeParameters lastMazeParameters;

    void generateAndDraw(){
        lastMaze = generateMaze();
        drawMaze(lastMaze);

        lastMazeParameters = new MazeParameters(lastMaze, lastMazeGenerator);
        lastMazeParameters.calculateParameters();
        printParameters(lastMazeParameters);

        MazeService.saveToText(MazeService.TEXT_DIRECTORY + MazeService.createTimeStampFileName(MazeService.TXT), lastMazeParameters);
        MazeService.saveAsImage(MazeService.IMAGE_DIRECTORY + MazeService.createTimeStampFileName(MazeService.PNG), canvas);
    }

    void printParameters(MazeParameters mazeParameters) {
        mazeParametersLabel.setText(mazeParameters.toParametersString());
    }

    private Maze generateMaze() {
        int width = 100;
        int height = 100;

        lastMazeGenerator = getMazeGenerator();
        return lastMazeGenerator.generate(width, height);
    }

    private MazeGenerator getMazeGenerator() {
        return new RandomWalkMazeGenerator();
        //return new RandomMazeGenerator();
    }

    private void saveToText() {
        if (null == lastMaze) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить лабиринт как текст");
        fileChooser.setInitialDirectory(new File(MazeService.TEXT_DIRECTORY));
        fileChooser.setInitialFileName(MazeService.createTimeStampFileName(MazeService.TXT));

        File file = fileChooser.showSaveDialog(Main.stage);
        if (null != file) {
            MazeService.saveAsImage(file.getAbsolutePath(), canvas);
        }
    }

    private boolean loadFromText() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить лабиринт из текстового файла");
        fileChooser.setInitialDirectory(new File(MazeService.TEXT_DIRECTORY));

        File file = fileChooser.showOpenDialog(Main.stage);
        if (null != file) {
            lastMazeParameters = MazeService.loadFromText(file.getAbsolutePath());

            lastMaze = lastMazeParameters.maze;
            lastMazeGenerator = lastMazeParameters.mazeGenerator;

            return true;
        } else {
            return false;
        }
    }

    private void loadFromTextAndDraw() {
        if (loadFromText()) {
            drawMaze(lastMaze);
        }
    }

    private void saveAsImage() {
        if (null == lastMaze) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить лабиринт как картинку");
        fileChooser.setInitialDirectory(new File(MazeService.IMAGE_DIRECTORY));
        fileChooser.setInitialFileName(MazeService.createTimeStampFileName(MazeService.PNG));

        File file = fileChooser.showSaveDialog(Main.stage);
        if (null != file) {
            MazeService.saveAsImage(file.getAbsolutePath(), canvas);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        canvas.setHeight(screenSize.height / 3.0);
        canvas.setWidth(screenSize.width / 3.0);

        generateButton.setText("Сгенерируй нам, мальчик!");

        generateButton.setOnAction(actionEvent -> {
            generateAndDraw();
        });

        saveToTextButton.setText("Сохранить как текст");
        saveToTextButton.setOnAction(actionEvent -> {
            saveToText();
        });

        loadFromTextButton.setText("Загрузить из текста");
        loadFromTextButton.setOnAction(actionEvent -> {
            loadFromTextAndDraw();
        });

        saveAsImageButton.setText("Сохранить как картинку");
        saveAsImageButton.setOnAction(actionEvent -> {
            saveAsImage();
        });
    }
}


package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    ListView<String> mazeType;

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

    private void drawMaze(Maze maze){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double cellWidth = canvas.getWidth() / maze.width;
        double cellHeight = canvas.getHeight() / maze.height;

        graphicsContext.setFill(Color.BLACK);

        for (int x = 0; x < maze.width; x++){
            for (int y = 0; y < maze.height; y++){
                boolean cell = maze.get(x, y);
                if (Maze.WALL == cell){
                    graphicsContext.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                }
            }
        }
    }

    Maze lastMaze;

    void generateAndDraw(){
        lastMaze = generateMaze();

        MazeService.saveToText(MazeService.TEXT_DIRECTORY + MazeService.createTimeStampFileName(MazeService.TXT), lastMaze);
        MazeService.saveAsImage(MazeService.IMAGE_DIRECTORY + MazeService.createTimeStampFileName(MazeService.PNG), canvas);

        drawMaze(lastMaze);
    }

    private Maze generateMaze() {
        int width = 100;
        int height = 100;
        MazeGenerator mazeGenerator = getMazeGenerator();
        return mazeGenerator.generate(width, height);
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
            lastMaze = MazeService.loadFromText(file.getAbsolutePath());
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

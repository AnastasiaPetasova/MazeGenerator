
package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import sample.generator.MazeGenerators;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    ListView<String> mazeGeneratorListView;

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

    @FXML
    TextField selectedLayerTextField;

    @FXML
    Button layerDownButton;

    @FXML
    Button layerUpButton;

    final int DOWN = 1, UP = 0;

    private void drawMazeHalf(
            GraphicsContext graphicsContext,
            double cellWidth, double cellHeight,
            int x, int y, int type) {
        double yStart = y * cellHeight + type * cellHeight / 2;
        graphicsContext.fillRect(
                x * cellWidth, yStart,
                cellWidth, cellHeight / 2);
    }

    private void drawMazeCell(GraphicsContext graphicsContext, double cellWidth, double cellHeight, int x, int y) {
        graphicsContext.fillRect(
                x * cellWidth, y * cellHeight,
                cellWidth, cellHeight);
    }

    private void drawMaze(Maze maze){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double cellWidth = canvas.getWidth() / maze.width;
        double cellHeight = canvas.getHeight() / maze.height;

        final int z = selectedLayer;

        for (int x = 0; x < maze.width; x++){
            for (int y = 0; y < maze.height; y++){
                if (maze.isEmpty(x, y, z)) {
                    graphicsContext.setFill(Color.WHITE);
                    drawMazeCell(graphicsContext, cellWidth, cellHeight, x, y);

                    if (z > 0 && maze.isEmpty(x, y, z - 1)) {
                        graphicsContext.setFill(Color.PURPLE);
                        drawMazeHalf(graphicsContext, cellWidth, cellHeight, x, y, DOWN);
                    }

                    if (z < maze.layersCount - 1 && maze.isEmpty(x, y, z + 1)) {
                        graphicsContext.setFill(Color.ORANGE);
                        drawMazeHalf(graphicsContext, cellWidth, cellHeight, x, y, UP);
                    }
                }
            }
        }

        List<Point3D> startFinishPath = maze.calculateShortestPaths()
                .calculateShortestPathTo(maze.finish);
        for (int pathIndex = 0; pathIndex < startFinishPath.size(); ++pathIndex) {
            Point3D cell = startFinishPath.get(pathIndex);

            if (z == cell.z) {
                graphicsContext.setFill(Color.YELLOW);
                drawMazeCell(graphicsContext, cellWidth, cellHeight, cell.x, cell.y);
            }

            if (pathIndex + 1 < startFinishPath.size()) {
                Point3D next = startFinishPath.get(pathIndex + 1);

                if (next.equalsTo(cell.x, cell.y, z - 1)) {
                    graphicsContext.setFill(Color.PURPLE);
                    drawMazeHalf(graphicsContext, cellWidth, cellHeight, cell.x, cell.y, DOWN);
                }
                if (next.equalsTo(cell.x, cell.y, z + 1)) {
                    graphicsContext.setFill(Color.ORANGE);
                    drawMazeHalf(graphicsContext, cellWidth, cellHeight, cell.x, cell.y, UP);
                }
            }
        }

        graphicsContext.setFill(Color.RED);

        if (z == maze.start.z) {
            drawMazeCell(graphicsContext, cellWidth, cellHeight, maze.start.x, maze.start.y);
        }

        if (z == maze.finish.z) {
            drawMazeCell(graphicsContext, cellWidth, cellHeight, maze.finish.x, maze.finish.y);
        }
    }

    MazeGenerator lastMazeGenerator;
    Maze lastMaze;
    MazeParameters lastMazeParameters;

    void generateAndDraw(){
        lastMaze = generateMaze();

        setLayer(1);
        layerUpButton.setDisable(false);

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
        int width = 25;
        int height = 25;
        int layersCount = 25;

        lastMazeGenerator = getMazeGenerator();
        return lastMazeGenerator.generate(width, height, layersCount);
    }

    private MazeGenerator getMazeGenerator() {
        int generatorIndex = mazeGeneratorListView.getSelectionModel().getSelectedIndex();
        return MazeGenerators.all().get(generatorIndex);
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
        initializeCanvas();

        initializeGeneratorListView();

        initializeButtons();

        initializeLayerElements();
    }

    private void initializeButtons() {
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

    private void initializeCanvas() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        canvas.setHeight(screenSize.height / 3.0);
        canvas.setWidth(screenSize.width / 3.0);
    }

    private void initializeGeneratorListView() {
        mazeGeneratorListView.setEditable(false);
        mazeGeneratorListView.setItems(
                FXCollections.observableArrayList(
                        MazeGenerators.all().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
                )
        );
        mazeGeneratorListView.getSelectionModel().selectLast();
    }

    private int selectedLayer;

    private void setLayer(int layer) {
        selectedLayer = layer;
        selectedLayerTextField.setText("" + selectedLayer);

        drawMaze(lastMaze);
    }

    private void initializeLayerElements() {
        layerDownButton.setDisable(true);
        layerDownButton.setOnAction(event -> {
            if (selectedLayer > 1) {
                setLayer(selectedLayer - 1);
                layerUpButton.setDisable(false);
            }

            if (1 == selectedLayer) {
                layerDownButton.setDisable(true);
            }
        });

        layerUpButton.setDisable(true);
        layerUpButton.setOnAction(event -> {
            if (selectedLayer + 1 < lastMaze.layersCount - 1) {
                setLayer(selectedLayer + 1);
                layerDownButton.setDisable(false);
            }

            if (lastMaze.layersCount - 2 == selectedLayer) {
                layerUpButton.setDisable(true);
            }
        });
    }
}

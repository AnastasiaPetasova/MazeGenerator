
package sample;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
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
    Label selectedLayerLabel;

    @FXML
    TextField selectedLayerTextField;

    @FXML
    Button layerDownButton;

    @FXML
    Button layerUpButton;

    @FXML
    Button goLeftButton;

    @FXML
    Button goRightButton;

    @FXML
    Button goDownButton;

    @FXML
    Button goUpButton;

    @FXML
    Button goLayerDownButton;

    @FXML
    Button goLayerUpButton;

    @FXML
    CheckBox userModeCheckBox;

    @FXML
    ComboBox<String> fogModeComboBox;

    @FXML
    CheckBox showPathCheckBox;

    final int DOWN = 0, UP = 1;

    private void drawMazeHalf(
            GraphicsContext graphicsContext,
            double cellWidth, double cellHeight,
            int x, int y, int type) {
        double yStart = y * cellHeight - type * cellHeight / 2;

        char direction = (DOWN == type ? 'v' : '^');

        int delta = (DOWN == type ? -1 : 1);

        graphicsContext.setLineWidth(1);

        graphicsContext.strokeText(
                "" + direction,
                x * cellWidth + cellWidth / 2, yStart + cellHeight + delta
        );

        graphicsContext.fillText(
                "" + direction,
                x * cellWidth + cellWidth / 2, yStart + cellHeight + delta
        );
    }

    private void drawMazeCell(GraphicsContext graphicsContext, double cellWidth, double cellHeight, int x, int y) {
        graphicsContext.strokeRect(
                x * cellWidth, y * cellHeight,
                cellWidth, cellHeight
        );
        graphicsContext.fillRect(
                x * cellWidth, y * cellHeight,
                cellWidth, cellHeight);
    }

    private void drawMaze(Maze maze){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        clearCanvas(graphicsContext);

        double cellWidth = canvas.getWidth() / maze.width;
        double cellHeight = canvas.getHeight() / maze.height;

        final int z = selectedLayer;

        drawLayer(maze, graphicsContext, cellWidth, cellHeight, z);

        if (needShowPath()) {
            drawShortestPath(maze, graphicsContext, cellWidth, cellHeight, z);
        }

        drawExits(maze, graphicsContext, cellWidth, cellHeight, z);

        if (isUserMode()) {
            drawUserPosition(maze, graphicsContext, cellWidth, cellHeight, z);
        }


    }

    private void drawUserPosition(Maze maze, GraphicsContext graphicsContext, double cellWidth, double cellHeight, int z) {
        if (null == userPosition) return;

        if (z != userPosition.z) return;

        graphicsContext.setStroke(Color.RED);
        graphicsContext.setLineWidth(2);

        graphicsContext.strokeRect(
                userPosition.x * cellWidth, userPosition.y * cellHeight, cellWidth, cellHeight
        );
    }

    private void drawExits(Maze maze, GraphicsContext graphicsContext, double cellWidth, double cellHeight, int z) {
        graphicsContext.setFill(Color.RED);

        if (z == maze.start.z) {
            drawMazeCell(graphicsContext, cellWidth, cellHeight, maze.start.x, maze.start.y);
        }

        if (z == maze.finish.z) {
            drawMazeCell(graphicsContext, cellWidth, cellHeight, maze.finish.x, maze.finish.y);
        }
    }

    private void clearCanvas(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawLayer(Maze maze, GraphicsContext graphicsContext, double cellWidth, double cellHeight, int z) {
        for (int x = 0; x < maze.width; x++){
            for (int y = 0; y < maze.height; y++){
                if (maze.isEmpty(x, y, z)) {
                    graphicsContext.setStroke(Color.BLACK);
                    graphicsContext.setFill(Color.WHITE);
                    drawMazeCell(graphicsContext, cellWidth, cellHeight, x, y);

                    graphicsContext.setFill(Color.BLACK);
                    if (z > 0 && maze.isEmpty(x, y, z - 1)) {
                        drawMazeHalf(graphicsContext, cellWidth, cellHeight, x, y, DOWN);
                    }

                    if (z < maze.layersCount - 1 && maze.isEmpty(x, y, z + 1)) {
                        drawMazeHalf(graphicsContext, cellWidth, cellHeight, x, y, UP);
                    }
                }
            }
        }
    }

    private void drawShortestPath(Maze maze, GraphicsContext graphicsContext, double cellWidth, double cellHeight, int z) {
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
                    drawMazeHalf(graphicsContext, cellWidth, cellHeight, cell.x, cell.y, DOWN);
                }
                if (next.equalsTo(cell.x, cell.y, z + 1)) {
                    drawMazeHalf(graphicsContext, cellWidth, cellHeight, cell.x, cell.y, UP);
                }
            }
        }
    }

    void drawLastMaze() {
        drawMaze(lastMaze);
    }

    MazeGenerator lastMazeGenerator;
    Maze lastMaze;
    MazeParameters lastMazeParameters;

    void initializeAndDraw(Maze maze){
        lastMaze = maze;

        // TODO move it all to setLayer
        setLayer(1);
        layerUpButton.setDisable(false);

        layerDownButton.setVisible(true);
        layerUpButton.setVisible(true);

        selectedLayerLabel.setVisible(true);
        selectedLayerTextField.setVisible(true);

        drawLastMaze();

        this.userPosition = new Point3D(lastMaze.start);
        updateUserElements();
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
            initializeAndDraw(lastMaze);
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

        initializeUserElements();
    }

    private void initializeButtons() {
        generateButton.setText("Сгенерируй нам, мальчик!");

        generateButton.setOnAction(actionEvent -> {
            initializeAndDraw(generateMaze());

            lastMazeParameters = new MazeParameters(lastMaze, lastMazeGenerator);
            lastMazeParameters.calculateParameters();
            printParameters(lastMazeParameters);

            MazeService.saveToText(MazeService.TEXT_DIRECTORY + MazeService.createTimeStampFileName(MazeService.TXT), lastMazeParameters);
            MazeService.saveAsImage(MazeService.IMAGE_DIRECTORY + MazeService.createTimeStampFileName(MazeService.PNG), canvas);
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
    }

    private void showDownLayer() {
        if (selectedLayer > 1) {
            setLayer(selectedLayer - 1);
            layerUpButton.setDisable(false);
        }

        if (1 == selectedLayer) {
            layerDownButton.setDisable(true);
        }
    }

    private void showUpLayer() {
        if (selectedLayer + 1 < lastMaze.layersCount - 1) {
            setLayer(selectedLayer + 1);
            layerDownButton.setDisable(false);
        }

        if (lastMaze.layersCount - 2 == selectedLayer) {
            layerUpButton.setDisable(true);
        }
    }

    private void initializeLayerElements() {
        layerUpButton.setVisible(false);
        layerDownButton.setVisible(false);

        layerDownButton.setDisable(true);
        layerDownButton.setOnAction(event -> {
            showDownLayer();
            drawLastMaze();
        });

        layerUpButton.setDisable(true);
        layerUpButton.setOnAction(event -> {
            showUpLayer();
            drawLastMaze();
        });

        selectedLayerLabel.setVisible(false);
        selectedLayerTextField.setVisible(false);
    }

    private boolean needShowPath() {
        return showPathCheckBox.isSelected();
    }

    private boolean isUserMode() {
        return userModeCheckBox.isSelected();
    }

    Point3D userPosition = null;

    private void updateUserElements() {
        userModeCheckBox.setVisible(null != userPosition);
        showPathCheckBox.setVisible(null != userPosition);

        boolean visible = null != userPosition && isUserMode();

        goUpButton.setVisible(visible);
        goDownButton.setVisible(visible);

        goLeftButton.setVisible(visible);
        goRightButton.setVisible(visible);

        goLayerDownButton.setVisible(visible);
        goLayerUpButton.setVisible(visible);

        fogModeComboBox.setVisible(visible);
    }

    private void updateUserState() {
        updateUserElements();
        drawLastMaze();
    }

    private void initializeUserElements() {
        showPathCheckBox.setOnAction(event -> {
            drawLastMaze();
        });

        userModeCheckBox.setOnAction(event -> {
            updateUserState();
        });

        goLeftButton.setOnAction(event -> {
            if (null == userPosition) return;

            if (0 == userPosition.x) return;
            if (lastMaze.isWall(userPosition.x - 1, userPosition.y, userPosition.z)) return;

            --userPosition.x;
            updateUserState();
        });

        goRightButton.setOnAction(event -> {
            if (null == userPosition) return;

            if (lastMaze.width - 1 == userPosition.x) return;
            if (lastMaze.isWall(userPosition.x + 1, userPosition.y, userPosition.z)) return;

            ++userPosition.x;
            updateUserState();
        });

        goUpButton.setOnAction(event -> {
            if (null == userPosition) return;

            if (0 == userPosition.y) return;
            if (lastMaze.isWall(userPosition.x, userPosition.y - 1, userPosition.z)) return;

            --userPosition.y;
            updateUserState();
        });

        goDownButton.setOnAction(event -> {
            if (null == userPosition) return;

            if (lastMaze.height - 1 == userPosition.y) return;
            if (lastMaze.isWall(userPosition.x, userPosition.y + 1, userPosition.z)) return;

            ++userPosition.y;
            updateUserState();
        });

        goLayerDownButton.setOnAction(event -> {
            if (null == userPosition) return;

            if (userPosition.z == 0 || lastMaze.isWall(userPosition.x, userPosition.y, userPosition.z - 1)) return;

            --userPosition.z;
            setLayer(selectedLayer - 1);

            updateUserState();
        });

        goLayerUpButton.setOnAction(event -> {
            if (null == userPosition) return;

            if (userPosition.z == lastMaze.layersCount - 1 || lastMaze.isWall(userPosition.x, userPosition.y, userPosition.z + 1)) return;

            ++userPosition.z;
            setLayer(selectedLayer + 1);

            updateUserState();
        });

        updateUserElements();
    }
}

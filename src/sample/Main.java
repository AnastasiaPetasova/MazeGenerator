package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

public class Main extends Application {

    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Main.stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("main_scene.fxml"));
        primaryStage.setTitle("Генератор 3D-лабиринтов");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double coeff = 2;
        primaryStage.setScene(new Scene(root, screenSize.width / coeff, screenSize.height / coeff));

        URL url = this.getClass().getResource("styles.css");
        if (url != null) {
            String css = url.toExternalForm();
            primaryStage.getScene().getStylesheets().add(css);
        }

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static int WIDTH = 1280;
    public static int HEIGHT = 720;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        stage.setTitle("Xếp thời khóa biểu SGU");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
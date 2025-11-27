package Interfaz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LauncherRedLogistica extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GUIs/RedLogistica.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Sistema de Optimización de Rutas - Dijkstra & Floyd-Warshall");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
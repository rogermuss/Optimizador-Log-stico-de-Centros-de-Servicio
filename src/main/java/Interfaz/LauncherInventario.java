package Interfaz;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LauncherInventario extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GUIs/Inventario.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setFullScreen(true);
        stage.setTitle("Inventario");
        stage.setScene(scene);
        stage.show();

    }



    //MAIN
    public static void main(String[] args) {
        launch(args);
    }
}


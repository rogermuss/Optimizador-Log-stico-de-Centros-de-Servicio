package Interfaz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPrincipalController {

    @FXML private ImageView fondoMenu;
    @FXML private Button botonOptimizar;
    @FXML private Button botonCreditos;
    @FXML private Button botonSalir;

    private static final String BOTON_NORMAL =
            "-fx-background-color: #1C6EA4;" +
                    "-fx-text-fill: white;" +
                    "-fx-border-color: #fff9af;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-radius: 14;" +
                    "-fx-font-weight: 700;" +
                    "-fx-font-size: 22px;" +
                    "-fx-focus-color: transparent;" +
                    "-fx-faint-focus-color: transparent;";

    private static final String BOTON_HOVER =
            "-fx-background-color: #33A1E0;" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-border-color: #fff9af;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-radius: 14;" +
                    "-fx-font-weight: 700;" +
                    "-fx-font-size: 22px;" +
                    "-fx-focus-color: transparent;" +
                    "-fx-faint-focus-color: transparent;";

    private static final String BOTON_PRESIONADO =
            "-fx-background-color: #154D71;" +
                    "-fx-text-fill: #ffffff;" +
                    "-fx-border-color: #33a1e0;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-radius: 14;" +
                    "-fx-font-weight: 700;" +
                    "-fx-font-size: 22px;" +
                    "-fx-focus-color: transparent;" +
                    "-fx-faint-focus-color: transparent;";

    private void aplicarNormal(Button b, DropShadow sombraNormal) {
        b.setStyle(BOTON_NORMAL);
        b.setTextFill(Color.WHITE);
        b.setEffect(sombraNormal);
    }

    private void configurarBoton(Button boton) {
        boton.setFocusTraversable(false);
        boton.setDefaultButton(false);
        boton.setCancelButton(false);

        DropShadow sombraNormal = new DropShadow();
        sombraNormal.setRadius(10);
        sombraNormal.setOffsetY(3);
        sombraNormal.setColor(Color.rgb(21, 77, 113, 0.40));

        DropShadow sombraHover = new DropShadow();
        sombraHover.setRadius(18);
        sombraHover.setOffsetY(6);
        sombraHover.setColor(Color.rgb(51, 161, 224, 0.70));

        DropShadow sombraPresionado = new DropShadow();
        sombraPresionado.setRadius(8);
        sombraPresionado.setOffsetY(2);
        sombraPresionado.setColor(Color.rgb(21, 77, 113, 0.85));

        aplicarNormal(boton, sombraNormal);

        boton.setOnMouseEntered(e -> {
            boton.setStyle(BOTON_HOVER);
            boton.setEffect(sombraHover);
            boton.setCursor(javafx.scene.Cursor.HAND);
        });
        boton.setOnMouseExited(e -> aplicarNormal(boton, sombraNormal));
        boton.setOnMousePressed(e -> {
            boton.setStyle(BOTON_PRESIONADO);
            boton.setEffect(sombraPresionado);
        });
        boton.setOnMouseReleased(e -> {
            if (boton.isHover()) {
                boton.setStyle(BOTON_HOVER);
                boton.setEffect(sombraHover);
            } else {
                aplicarNormal(boton, sombraNormal);
            }
        });
    }

    @FXML
    private void initialize() {
        fondoMenu.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                fondoMenu.fitWidthProperty().bind(scene.widthProperty());
                fondoMenu.fitHeightProperty().bind(scene.heightProperty());
            }
        });

        configurarBoton(botonOptimizar);
        configurarBoton(botonCreditos);
        configurarBoton(botonSalir);
    }

    @FXML
    private void alDarOptimizar(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                GraphExplorerAPP.class.getResource("/GUIs/SeleccionProblema.fxml")
        );
        Parent raiz = loader.load();
        Stage venActual = (Stage) botonOptimizar.getScene().getWindow();
        Stage venNuevo = new Stage();
        venNuevo.setTitle("GraphExplorer | Selección de Problema");
        venNuevo.setScene(new Scene(raiz, venActual.getWidth(), venActual.getHeight()));
        venNuevo.setMaximized(venActual.isMaximized());
        venNuevo.setFullScreen(venActual.isFullScreen());
        venActual.close();
        venNuevo.show();
    }

    @FXML
    private void alDarCreditos(ActionEvent e) {
        Stage stage = (Stage) botonCreditos.getScene().getWindow();
        var alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alerta.initOwner(stage);
        alerta.initModality(javafx.stage.Modality.WINDOW_MODAL);
        alerta.initStyle(javafx.stage.StageStyle.UTILITY);
        alerta.setTitle("Créditos");
        alerta.setHeaderText("Proyecto Final de Algoritmos | Créditos");
        alerta.setContentText(
                "Equipo:\n" +
                        " - David Escarcega Schammler\n" +
                        " - Jose Rogelio Escobar Hernandez\n" +
                        " - Angel Gabriel Manjarrez Moreno\n\n" +
                        "Materia: Algoritmos y Estructuras de Datos\n" +
                        "Práctica: Proyecto Final - Grafos y Árboles"
        );
        alerta.showAndWait();
    }

    @FXML
    private void alDarSalir(ActionEvent e) {
        Stage stage = (Stage) botonSalir.getScene().getWindow();
        stage.close();
        Platform.exit();
    }
}

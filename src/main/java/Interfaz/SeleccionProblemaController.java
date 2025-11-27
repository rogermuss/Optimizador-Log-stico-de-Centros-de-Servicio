package Interfaz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class SeleccionProblemaController {
    @FXML private VBox columnaProblema1;
    @FXML private VBox columnaProblema2;
    @FXML private VBox columnaProblema3;

    @FXML private ImageView gifProblema1;
    @FXML private ImageView gifProblema2;
    @FXML private ImageView gifProblema3;

    @FXML private Button botonProblema1;
    @FXML private Button botonProblema2;
    @FXML private Button botonProblema3;
    @FXML
    private Button botonSalirSeleccion;

    private static final String COLUMNA_NORMAL =
            "-fx-background-color: rgba(21,77,113,0.80);" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-radius: 18;";

    private static final String COLUMNA_HOVER =
            "-fx-background-color: rgba(51,161,224,0.95);" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-radius: 18;" +
                    "-fx-border-color: #FFF9AF;" +
                    "-fx-border-width: 2;";

    @FXML
    private void initialize() {
        configurarColumna(columnaProblema1, gifProblema1);
        configurarColumna(columnaProblema2, gifProblema2);
        configurarColumna(columnaProblema3, gifProblema3);
    }

    private void configurarColumna(VBox columna, ImageView gif) {
        columna.setStyle(COLUMNA_NORMAL);
        columna.setCursor(Cursor.HAND);

        DropShadow sombraNormal = new DropShadow();
        sombraNormal.setRadius(14);
        sombraNormal.setOffsetY(4);
        sombraNormal.setColor(Color.rgb(0, 0, 0, 0.35));

        DropShadow sombraHover = new DropShadow();
        sombraHover.setRadius(24);
        sombraHover.setOffsetY(8);
        sombraHover.setColor(Color.rgb(0, 0, 0, 0.55));

        columna.setEffect(sombraNormal);
        if (gif != null) {
            gif.setOpacity(0.35);
        }

        columna.setOnMouseEntered(e -> {
            columna.setStyle(COLUMNA_HOVER);
            columna.setEffect(sombraHover);
            if (gif != null) {
                gif.setOpacity(0.95);
            }
        });

        columna.setOnMouseExited(e -> {
            columna.setStyle(COLUMNA_NORMAL);
            columna.setEffect(sombraNormal);
            if (gif != null) {
                gif.setOpacity(0.35);
            }
        });
    }

    @FXML
    private void alElegirProblema1(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                GraphExplorerAPP.class.getResource("/GUIs/RedLogistica.fxml")
        );
        Parent raiz = loader.load();
        Stage venActual = (Stage) botonProblema1.getScene().getWindow();
        Stage venNuevo = new Stage();
        venNuevo.setTitle("GraphExplorer | Problema de Rutas y Distancias");
        venNuevo.setScene(new Scene(raiz, venActual.getWidth(), venActual.getHeight()));
        venNuevo.setMaximized(venActual.isMaximized());
        venNuevo.setFullScreen(venActual.isFullScreen());
        venActual.close();
        venNuevo.setFullScreenExitHint("");
        venNuevo.show();
    }

    @FXML
    private void alElegirProblema2(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                GraphExplorerAPP.class.getResource("/GUIs/InfraestructuraYConexion.fxml")
        );
        Parent raiz = loader.load();
        Stage venActual = (Stage) botonProblema2.getScene().getWindow();
        Stage venNuevo = new Stage();
        venNuevo.setTitle("GraphExplorer | Problemas de Infraestructura y Conexion");
        venNuevo.setScene(new Scene(raiz, venActual.getWidth(), venActual.getHeight()));
        venNuevo.setMaximized(venActual.isMaximized());
        venNuevo.setFullScreen(venActual.isFullScreen());
        venActual.close();
        venNuevo.setFullScreenExitHint("");
        venNuevo.show();
    }

    @FXML
    private void alElegirProblema3(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                GraphExplorerAPP.class.getResource("/GUIs/Inventario.fxml")
        );
        Parent raiz = loader.load();
        Stage venActual = (Stage) botonProblema3.getScene().getWindow();
        Stage venNuevo = new Stage();
        venNuevo.setTitle("GraphExplorer | Problemas de Inventario y orden");
        venNuevo.setScene(new Scene(raiz, venActual.getWidth(), venActual.getHeight()));
        venNuevo.setMaximized(venActual.isMaximized());
        venNuevo.setFullScreen(venActual.isFullScreen());
        venActual.close();
        venNuevo.setFullScreenExitHint("");
        venNuevo.show();
    }

    @FXML
    private void alDarSalir(javafx.event.ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GraphExplorerAPP.class.getResource("/GUIs/MenuPrincipal.fxml")
            );
            Parent raiz = loader.load();

            Stage venActual = (Stage) botonSalirSeleccion.getScene().getWindow();
            Stage venNuevo = new Stage();
            venNuevo.setTitle("GraphExplorer | Menú principal");
            venNuevo.setScene(new Scene(raiz, venActual.getWidth(), venActual.getHeight()));
            venNuevo.setMaximized(venActual.isMaximized());
            venNuevo.setFullScreen(venActual.isFullScreen());
            venActual.close();
            venNuevo.setFullScreenExitHint("");
            venNuevo.show();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
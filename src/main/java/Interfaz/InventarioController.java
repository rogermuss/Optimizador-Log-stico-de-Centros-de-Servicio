package Interfaz;

import Data.LectorDeDatos;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {

    @FXML private Button botonReturn;
    @FXML private Button botonRecorrido;
    @FXML private Button botonCargar;
    @FXML private Button botonAplicarConsulta;

    @FXML private TableView<String[]> tablaDatos;
    @FXML private ComboBox<String> selectorRecorrido;
    @FXML private TextField textFieldConsulta;

    private final LectorDeDatos lectorDeDatos = new LectorDeDatos();

    // Variables principales
    private String[] encabezadosOriginales;
    private List<String[]> datosCompletos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        selectorRecorrido.setDisable(true);
        botonAplicarConsulta.setDisable(true);

        botonCargar.setOnAction(e -> abrirExplorador());
    }


    private void abrirExplorador() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv")
        );

        Stage stage = (Stage) botonCargar.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            registrarLog("Archivo seleccionado: " + archivo.getAbsolutePath());

            try (InputStream stream = new FileInputStream(archivo)) {

                lectorDeDatos.cargarDatos(stream);

                encabezadosOriginales = lectorDeDatos.getEncabezados();
                datosCompletos = lectorDeDatos.getDatos();

                if (encabezadosOriginales == null || datosCompletos == null) {
                    registrarLog("Archivo CSV vacío o con formato incorrecto.");
                    return;
                }

                registrarLog("Columnas detectadas:");
                for (String encabezado : encabezadosOriginales) {
                    registrarLog(" - " + encabezado);
                }

                registrarLog("Filas leídas: " + datosCompletos.size());

                rellenarTabView();

                llenarComBox();

                selectorRecorrido.setDisable(false);
                botonAplicarConsulta.setDisable(false);

            } catch (IOException e) {
                registrarLog("Error al leer el archivo: " + e.getMessage());
            }
        }
    }


    private void rellenarTabView() {

        tablaDatos.getColumns().clear();

        for (int i = 0; i < encabezadosOriginales.length; i++) {
            final int indice = i;

            TableColumn<String[], String> columna = new TableColumn<>(encabezadosOriginales[i]);

            columna.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            indice < data.getValue().length ?
                                    data.getValue()[indice] : ""
                    )
            );

            tablaDatos.getColumns().add(columna);
        }

        ObservableList<String[]> datosTabla =
                FXCollections.observableArrayList(datosCompletos);

        tablaDatos.setItems(datosTabla);
    }


    private void llenarComBox() {
        selectorRecorrido.getItems().clear();
        selectorRecorrido.getItems().addAll(encabezadosOriginales);
    }


    private void registrarLog(String mensaje) {
        System.out.println(mensaje);
    }



    private void cargarDatosAutomaticamente() {
        // Implementar si deseas carga automática
    }
}

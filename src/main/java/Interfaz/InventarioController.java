package Interfaz;

import Data.LectorDeDatos;
import Data.Producto;
import EstructurasDeDatos.ArbolAVL;
import EstructurasDeDatos.Node;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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

    private String[] encabezadosOriginales;
    private List<String[]> datosCompletos;
    private ArbolAVL<Producto> arbolInventario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Desactivo botones si no hay archivo
        selectorRecorrido.setDisable(true);
        botonAplicarConsulta.setDisable(true);
        botonRecorrido.setDisable(true);

        botonCargar.setOnAction(e -> abrirExplorador());

        botonAplicarConsulta.setOnAction(e -> buscarProducto());
        botonRecorrido.setOnAction(e -> mostrarRecorrido());
        botonReturn.setOnAction(e -> regresarAlMenu());
        selectorRecorrido.getItems().clear();
        selectorRecorrido.getItems().addAll(
                "InOrden (Ascendente)",
                "PreOrden",
                "PostOrden"
        );
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
            try (InputStream stream = new FileInputStream(archivo)) {
                lectorDeDatos.cargarDatos(stream);

                encabezadosOriginales = lectorDeDatos.getEncabezados();
                datosCompletos = lectorDeDatos.getDatos();

                if (encabezadosOriginales == null || datosCompletos == null) {
                    mostrarAlerta("Error", "Archivo CSV vacío o con formato incorrecto.");
                    return;
                }

                rellenarTabView();
                cargarProductosEnArbol();

                selectorRecorrido.setDisable(false);
                botonAplicarConsulta.setDisable(false);
                botonRecorrido.setDisable(false);

            } catch (IOException e) {
                mostrarAlerta("Error", "Error al leer el archivo: " + e.getMessage());
            }
        }
    }

    private void cargarProductosEnArbol() {

        arbolInventario = new ArbolAVL<>((p1, p2) ->
                p1.getId().compareTo(p2.getId())
        );

        //
        for (String[] fila : datosCompletos) {
            try {
                Producto producto = new Producto(
                        fila[0].trim(),
                        fila[1].trim(),
                        Integer.parseInt(fila[2].trim()),
                        fila[3].trim()
                );

                Node<Producto> nuevoRoot = arbolInventario.insertarNodo(
                        arbolInventario.getRoot(),
                        producto
                );
                arbolInventario.setRoot(nuevoRoot);

            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            }
        }
    }

    private void buscarProducto() {
        String idBuscar = textFieldConsulta.getText().trim();

        if (idBuscar.isEmpty()) {
            mostrarAlerta("Advertencia", "Debe ingresar un ID de producto");
            return;
        }

        Producto encontrado = buscarEnArbol(arbolInventario.getRoot(), idBuscar);

        if (encontrado != null) {
            mostrarAlerta("Producto Encontrado", encontrado.toString());
        } else {
            mostrarAlerta("No Encontrado", "El producto con ID '" + idBuscar + "' no existe");
        }
    }

    private Producto buscarEnArbol(Node<Producto> nodo, String idBuscado) {
        if (nodo == null) {
            return null;
        }

        int comparacion = idBuscado.compareTo(nodo.getItem().getId());

        if (comparacion == 0) {
            return nodo.getItem();
        } else if (comparacion < 0) {
            return buscarEnArbol(nodo.getLeft(), idBuscado);
        } else {
            return buscarEnArbol(nodo.getRight(), idBuscado);
        }
    }

    private void mostrarRecorrido() {
        String tipoRecorrido = selectorRecorrido.getValue();

        if (tipoRecorrido == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un tipo de recorrido");
            return;
        }

        if (arbolInventario == null || arbolInventario.getRoot() == null) {
            mostrarAlerta("Advertencia", "No hay datos en el árbol");
            return;
        }

        List<Producto> resultado = new ArrayList<>();

        switch (tipoRecorrido) {
            case "InOrden (Ascendente)":
                inOrdenLista(arbolInventario.getRoot(), resultado);
                break;
            case "PreOrden":
                preOrdenLista(arbolInventario.getRoot(), resultado);
                break;
            case "PostOrden":
                postOrdenLista(arbolInventario.getRoot(), resultado);
                break;
        }

        actualizarTablaConRecorrido(resultado);
    }

    private void inOrdenLista(Node<Producto> nodo, List<Producto> resultado) {
        if (nodo != null) {
            inOrdenLista(nodo.getLeft(), resultado);
            resultado.add(nodo.getItem());
            inOrdenLista(nodo.getRight(), resultado);
        }
    }

    private void preOrdenLista(Node<Producto> nodo, List<Producto> resultado) {
        if (nodo != null) {
            resultado.add(nodo.getItem());
            preOrdenLista(nodo.getLeft(), resultado);
            preOrdenLista(nodo.getRight(), resultado);
        }
    }

    private void postOrdenLista(Node<Producto> nodo, List<Producto> resultado) {
        if (nodo != null) {
            postOrdenLista(nodo.getLeft(), resultado);
            postOrdenLista(nodo.getRight(), resultado);
            resultado.add(nodo.getItem());
        }
    }

    private void actualizarTablaConRecorrido(List<Producto> productos) {
        List<String[]> datosOrdenados = new ArrayList<>();

        for (Producto p : productos) {
            datosOrdenados.add(new String[]{
                    p.getId(),
                    p.getNombre(),
                    String.valueOf(p.getStock()),
                    p.getUbicacion()
            });
        }

        ObservableList<String[]> datosTabla =
                FXCollections.observableArrayList(datosOrdenados);
        tablaDatos.setItems(datosTabla);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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

    private void regresarAlMenu() {
        FXMLLoader loader = new FXMLLoader(
                GraphExplorerAPP.class.getResource("/GUIs/SeleccionProblema.fxml")
        );
        Parent raiz = null;
        try {
            raiz = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage venActual = (Stage) botonReturn.getScene().getWindow();
        Stage venNuevo = new Stage();
        venNuevo.setTitle("GraphExplorer | Selección de Problema");
        venNuevo.setScene(new Scene(raiz, venActual.getWidth(), venActual.getHeight()));
        venNuevo.setMaximized(venActual.isMaximized());
        venNuevo.setFullScreen(venActual.isFullScreen());
        venActual.close();
        venNuevo.setFullScreenExitHint("");
        venNuevo.show();
    }

}
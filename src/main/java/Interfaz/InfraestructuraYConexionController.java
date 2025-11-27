package Interfaz;

import Algorithms.AlgoritmoDePrim;
import Algorithms.AlgoritmoDePrim.ResultadoPrim;
import Data.LectorDeDatos;
import EstructurasDeDatos.Arista;
import EstructurasDeDatos.Grafo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class InfraestructuraYConexionController implements Initializable {

    @FXML private Button botonCargarRed;
    @FXML private Button botonEjecutarPrim;
    @FXML private Button botonRegresarMenu;

    @FXML private TextArea areaDetalles;

    @FXML private TableView<String[]> tablaDatos;
    @FXML private TableView<String[]> tablaResultadosMST;

    @FXML private Label labelCostoTotal;

    @FXML private TabPane tabPanePrincipal;
    @FXML private Tab tabDatos;
    @FXML private Tab tabResultados;

    @FXML private Pane panelGrafoMST;

    private final LectorDeDatos lectorDeDatos = new LectorDeDatos();
    private String[] encabezados;
    private List<String[]> filasDatos;

    private Grafo grafo;
    private Map<String, Integer> nombreAIndice;
    private Map<Integer, String> indiceANombre;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nombreAIndice = new HashMap<>();
        indiceANombre = new HashMap<>();

        botonEjecutarPrim.setDisable(true);

        inicializarTextoExplicacion();

        botonCargarRed.setOnAction(e -> cargarRedDesdeCSV());
        botonEjecutarPrim.setOnAction(e -> ejecutarPrim());
        botonRegresarMenu.setOnAction(e -> regresarAlMenu());
    }

    private void inicializarTextoExplicacion() {
        String texto =
                "ALGORITMO DE PRIM\n" +
                        "-------------------------\n\n" +
                        "• Conecta todos los centros usando rutas de menor costo.\n" +
                        "• Siempre agrega la conexión más barata que une un centro nuevo.\n" +
                        "• El resultado es una red sin ciclos y con costo total mínimo.\n";
        areaDetalles.setText(texto);
    }

    private void cargarRedDesdeCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV de Red de Conexión");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv")
        );

        Stage stage = (Stage) botonCargarRed.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            try (InputStream stream = new FileInputStream(archivo)) {
                lectorDeDatos.cargarDatos(stream);

                encabezados = lectorDeDatos.getEncabezados();
                filasDatos = lectorDeDatos.getDatos();

                if (encabezados == null || filasDatos == null || filasDatos.isEmpty()) {
                    mostrarAlerta("Error", "El archivo CSV está vacío o mal formado.");
                    return;
                }

                if (!validarFormatoCSV()) {
                    mostrarAlerta("Error",
                            "El CSV debe tener al menos 3 columnas: Origen, Destino, Costo.\n" +
                                    "El Tipo de Ruta (si existe) se ignora para el cálculo.");
                    return;
                }

                rellenarTablaDatos();
                construirGrafoDesdeCSV();

                botonEjecutarPrim.setDisable(false);
                panelGrafoMST.getChildren().clear();
                tablaResultadosMST.getItems().clear();
                labelCostoTotal.setText("Costo total mínimo de conexión: -");

                mostrarAlerta("Éxito",
                        "Red de conexión cargada correctamente.\n" +
                                "Vértices (ciudades): " + nombreAIndice.size() + "\n" +
                                "Aristas (rutas posibles): " + filasDatos.size());

                if (tabPanePrincipal != null && tabDatos != null) {
                    tabPanePrincipal.getSelectionModel().select(tabDatos);
                }

            } catch (IOException e) {
                mostrarAlerta("Error", "Error al leer el archivo: " + e.getMessage());
            }
        }
    }

    private boolean validarFormatoCSV() {
        if (encabezados.length < 3) {
            return false;
        }
        for (String[] fila : filasDatos) {
            if (fila.length < 3) {
                return false;
            }
        }
        return true;
    }

    private void construirGrafoDesdeCSV() {
        nombreAIndice.clear();
        indiceANombre.clear();

        Set<String> ciudadesUnicas = new HashSet<>();

        for (String[] fila : filasDatos) {
            String origen = fila[0].trim();
            String destino = fila[1].trim();
            if (!origen.isEmpty()) ciudadesUnicas.add(origen);
            if (!destino.isEmpty()) ciudadesUnicas.add(destino);
        }

        int indice = 0;
        for (String ciudad : ciudadesUnicas) {
            nombreAIndice.put(ciudad, indice);
            indiceANombre.put(indice, ciudad);
            indice++;
        }

        grafo = new Grafo(ciudadesUnicas.size());

        for (String[] fila : filasDatos) {
            try {
                String origen = fila[0].trim();
                String destino = fila[1].trim();
                String costoStr = fila[2].trim();

                if (origen.isEmpty() || destino.isEmpty() || costoStr.isEmpty()) {
                    continue;
                }

                int u = nombreAIndice.get(origen);
                int v = nombreAIndice.get(destino);
                double costo = Double.parseDouble(costoStr);

                grafo.agregarArista(u, v, costo);

            } catch (Exception e) {
                System.err.println("Error procesando fila: " + Arrays.toString(fila));
            }
        }
    }

    private void rellenarTablaDatos() {
        tablaDatos.getColumns().clear();

        for (int i = 0; i < encabezados.length; i++) {
            final int indiceColumna = i;
            TableColumn<String[], String> columna = new TableColumn<>(encabezados[i]);
            columna.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            indiceColumna < data.getValue().length
                                    ? data.getValue()[indiceColumna]
                                    : ""
                    )
            );
            tablaDatos.getColumns().add(columna);
        }

        ObservableList<String[]> datosTabla = FXCollections.observableArrayList(filasDatos);
        tablaDatos.setItems(datosTabla);
    }

    private void ejecutarPrim() {
        if (grafo == null || grafo.getNumVertices() == 0) {
            mostrarAlerta("Advertencia", "Primero cargue una red de conexión válida.");
            return;
        }

        AlgoritmoDePrim prim = new AlgoritmoDePrim();
        ResultadoPrim resultado = prim.calcularMST(grafo, 0);

        mostrarResultadosPrim(resultado);
        dibujarMST(resultado);

        if (tabPanePrincipal != null && tabResultados != null) {
            tabPanePrincipal.getSelectionModel().select(tabResultados);
        }
    }

    private void mostrarResultadosPrim(ResultadoPrim resultado) {
        tablaResultadosMST.getColumns().clear();

        TableColumn<String[], String> colOrden = new TableColumn<>("Orden");
        colOrden.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> colColor = new TableColumn<>("Color");
        colColor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colOrigen = new TableColumn<>("Ciudad Origen");
        colOrigen.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));

        TableColumn<String[], String> colDestino = new TableColumn<>("Ciudad Destino");
        colDestino.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));

        TableColumn<String[], String> colCosto = new TableColumn<>("Costo");
        colCosto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[4]));

        tablaResultadosMST.getColumns().addAll(colOrden, colColor, colOrigen, colDestino, colCosto);

        String[] nombresColores = {
                "Verde", "Amarillo", "Cian", "Rosa", "Morado", "Naranja", "Azul", "Rojo"
        };

        List<String[]> filasMST = new ArrayList<>();
        List<Arista> aristas = resultado.getAristasMST();

        for (int i = 0; i < aristas.size(); i++) {
            Arista a = aristas.get(i);
            String origenNombre = indiceANombre.get(a.getOrigen());
            String destinoNombre = indiceANombre.get(a.getDestino());
            String costoStr = String.valueOf(a.getPeso());
            String nombreColor = nombresColores[i % nombresColores.length];

            filasMST.add(new String[]{
                    String.valueOf(i + 1),
                    nombreColor,
                    origenNombre,
                    destinoNombre,
                    costoStr
            });
        }

        ObservableList<String[]> datosMST = FXCollections.observableArrayList(filasMST);
        tablaResultadosMST.setItems(datosMST);

        labelCostoTotal.setText("Costo total mínimo de conexión: " + resultado.getCostoTotal());

        String texto =
                "RESULTADOS DEL ÁRBOL DE EXPANSIÓN MÍNIMA\n" +
                        "----------------------------------------\n\n" +
                        "Centros conectados: " + grafo.getNumVertices() + "\n" +
                        "Conexiones usadas: " + resultado.getAristasMST().size() + "\n" +
                        "Costo total mínimo: " + resultado.getCostoTotal() + "\n";
        areaDetalles.setText(texto);
    }


    private void dibujarMST(ResultadoPrim resultado) {
        if (grafo == null || panelGrafoMST == null || resultado == null) return;

        panelGrafoMST.getChildren().clear();

        int n = grafo.getNumVertices();
        if (n == 0) return;

        double width = panelGrafoMST.getWidth() > 0 ? panelGrafoMST.getWidth() : panelGrafoMST.getPrefWidth();
        double height = panelGrafoMST.getHeight() > 0 ? panelGrafoMST.getHeight() : panelGrafoMST.getPrefHeight();

        if (width <= 0) width = 800;
        if (height <= 0) height = 340;

        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double radio = Math.min(width, height) * 0.45;

        double[] xs = new double[n];
        double[] ys = new double[n];

        for (int i = 0; i < n; i++) {
            double ang = 2 * Math.PI * i / n;
            xs[i] = centerX + radio * Math.cos(ang);
            ys[i] = centerY + radio * Math.sin(ang);
        }

        Color[] colores = new Color[]{
                Color.web("#33FF99"),
                Color.web("#F4D35E"),
                Color.web("#4CC9F0"),
                Color.web("#FF006E"),
                Color.web("#9B5DE5"),
                Color.web("#FF9F1C"),
                Color.web("#4895EF"),
                Color.web("#EF476F")
        };

        List<Arista> aristas = resultado.getAristasMST();

        for (int i = 0; i < aristas.size(); i++) {
            Arista a = aristas.get(i);
            int u = a.getOrigen();
            int v = a.getDestino();

            double x1 = xs[u];
            double y1 = ys[u];
            double x2 = xs[v];
            double y2 = ys[v];

            Color colorLinea = colores[i % colores.length];

            Line linea = new Line(x1, y1, x2, y2);
            linea.setStroke(colorLinea);
            linea.setStrokeWidth(3.0);

            double midX = (x1 + x2) / 2.0;
            double midY = (y1 + y2) / 2.0;

            Text ordenTexto = new Text(midX - 4, midY - 4, String.valueOf(i + 1));
            ordenTexto.setFill(colorLinea);
            ordenTexto.setFont(Font.font(11));

            panelGrafoMST.getChildren().addAll(linea, ordenTexto);
        }

        for (int i = 0; i < n; i++) {
            Circle c = new Circle(xs[i], ys[i], 13, Color.web("#FFF9AF"));
            c.setStroke(Color.web("#0F3048"));
            c.setStrokeWidth(2);

            String nombreCiudad = indiceANombre.get(i);
            if (nombreCiudad == null) {
                nombreCiudad = "N" + i;
            }

            Text t = new Text(xs[i] - 28, ys[i] + 25, nombreCiudad);
            t.setFill(Color.WHITE);
            t.setFont(Font.font(11));

            panelGrafoMST.getChildren().addAll(c, t);
        }
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void regresarAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    GraphExplorerAPP.class.getResource("/GUIs/SeleccionProblema.fxml")
            );
            Parent raiz = loader.load();

            Stage venActual = (Stage) botonRegresarMenu.getScene().getWindow();
            Stage venNuevo = new Stage();
            venNuevo.setTitle("GraphExplorer | Selección de Problema");
            venNuevo.setScene(new Scene(raiz, venActual.getWidth(), venActual.getHeight()));
            venNuevo.setMaximized(venActual.isMaximized());
            venNuevo.setFullScreen(venActual.isFullScreen());
            venActual.close();
            venNuevo.setFullScreenExitHint("");
            venNuevo.show();

        } catch (IOException e) {
            throw new RuntimeException("Error al regresar al menú principal", e);
        }
    }
}

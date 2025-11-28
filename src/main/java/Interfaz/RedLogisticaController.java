package Interfaz;

import Algorithms.Dijkstra;
import Algorithms.FloydWarshall;
import Algorithms.ResultadoDijkstra;
import Algorithms.ResultadoFloydWarshall;
import Data.LectorDeDatos;
import EstructurasDeDatos.GrafoLista;
import EstructurasDeDatos.GrafoMatriz;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class RedLogisticaController implements Initializable {

    @FXML private Button botonReturn;
    @FXML private Button botonCargar;
    @FXML private Button botonEjecutarAlgoritmo;

    @FXML private TableView<String[]> tablaDatos;
    @FXML private TableView<String[]> tablaResultados;
    @FXML private ComboBox<String> selectorAlgoritmo;
    @FXML private ComboBox<Integer> selectorOrigen;
    @FXML private ComboBox<Integer> selectorDestino;
    @FXML private Label labelOrigen;
    @FXML private Label labelDestino;
    @FXML private TextArea areaDetalles;
    @FXML private VBox panelParametros;

    private final LectorDeDatos lectorDeDatos = new LectorDeDatos();
    private String[] encabezadosOriginales;
    private List<String[]> datosCompletos;

    private GrafoLista grafoLista;
    private GrafoMatriz grafoMatriz;
    private int numeroVertices = 0;
    private Map<String, Integer> nombreAIndice;
    private Map<Integer, String> indiceANombre;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nombreAIndice = new HashMap<>();
        indiceANombre = new HashMap<>();


        panelParametros.setVisible(false);

        selectorAlgoritmo.getItems().addAll(
                "Dijkstra Iterativo - Ruta Más Corta (Un Origen)",
                "Dijkstra Recursivo - Ruta Más Corta (Un Origen)",
                "Floyd-Warshall - Análisis de Conectividad Total"
        );

        selectorAlgoritmo.setOnAction(e -> cambiarAlgoritmo());
        selectorOrigen.setOnAction(e -> {
            verificarParametrosDijkstra();
            if (selectorAlgoritmo.getValue() != null &&
                    selectorAlgoritmo.getValue().contains("Dijkstra")) {
                ejecutarDijkstra();
            }
        });
        selectorDestino.setOnAction(e -> {
            if (selectorAlgoritmo.getValue() != null &&
                    selectorAlgoritmo.getValue().contains("Dijkstra")) {
                ejecutarDijkstra();
            }
        });

        botonCargar.setOnAction(e -> abrirExplorador());
        botonEjecutarAlgoritmo.setOnAction(e -> ejecutarAlgoritmo());
        botonReturn.setOnAction(e -> regresarAlMenu());
    }

    private void verificarParametrosDijkstra() {
        String algoritmoSeleccionado = selectorAlgoritmo.getValue();
        if (algoritmoSeleccionado != null && algoritmoSeleccionado.contains("Dijkstra")) {
            Integer origen = selectorOrigen.getValue();
        }
    }

    private void abrirExplorador() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo CSV de Red Logística");
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

                if (encabezadosOriginales == null || datosCompletos == null || datosCompletos.isEmpty()) {
                    mostrarAlerta("Error", "Archivo CSV vacío o con formato incorrecto.");
                    return;
                }

                if (encabezadosOriginales.length < 3) {
                    mostrarAlerta("Error", "El CSV debe tener al menos 3 columnas: Origen, Destino, Costo");
                    return;
                }

                rellenarTabView();
                construirGrafos();
                selectorAlgoritmo.setDisable(false);

                mostrarAlerta("Éxito", "Red logística cargada correctamente.\n" +
                        "Vértices: " + numeroVertices + "\n" +
                        "Aristas: " + datosCompletos.size());

            } catch (IOException e) {
                mostrarAlerta("Error", "Error al leer el archivo: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                mostrarAlerta("Error", "Error procesando datos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void construirGrafos() {
        Set<String> verticesUnicos = new HashSet<>();

        for (String[] fila : datosCompletos) {
            if (fila.length >= 3) {
                String origen = fila[0].trim();
                String destino = fila[1].trim();

                if (!origen.isEmpty() && !destino.isEmpty()) {
                    verticesUnicos.add(origen);
                    verticesUnicos.add(destino);
                }
            }
        }

        numeroVertices = verticesUnicos.size();

        if (numeroVertices == 0) {
            mostrarAlerta("Error", "No se encontraron vértices válidos en el archivo.");
            return;
        }

        int indice = 0;
        List<String> verticesOrdenados = new ArrayList<>(verticesUnicos);
        Collections.sort(verticesOrdenados);

        nombreAIndice.clear();
        indiceANombre.clear();

        for (String nombreVertice : verticesOrdenados) {
            nombreAIndice.put(nombreVertice, indice);
            indiceANombre.put(indice, nombreVertice);
            indice++;
        }

        grafoLista = new GrafoLista(numeroVertices);
        grafoMatriz = new GrafoMatriz(numeroVertices);

        int aristasAgregadas = 0;
        for (String[] fila : datosCompletos) {
            try {
                if (fila.length < 3) {
                    System.err.println("Fila incompleta: " + Arrays.toString(fila));
                    continue;
                }

                String origen = fila[0].trim();
                String destino = fila[1].trim();
                String costoStr = fila[2].trim();

                if (origen.isEmpty() || destino.isEmpty() || costoStr.isEmpty()) {
                    System.err.println("Fila con datos vacíos: " + Arrays.toString(fila));
                    continue;
                }

                double costoDouble = Double.parseDouble(costoStr);
                int peso = (int) Math.round(costoDouble);

                if (peso <= 0) {
                    System.err.println("Costo inválido (<= 0): " + Arrays.toString(fila));
                    continue;
                }

                Integer indiceOrigen = nombreAIndice.get(origen);
                Integer indiceDestino = nombreAIndice.get(destino);

                if (indiceOrigen == null || indiceDestino == null) {
                    System.err.println("Nodo no encontrado: " + Arrays.toString(fila));
                    continue;
                }

                grafoLista.agregarArista(indiceOrigen, indiceDestino, peso);
                grafoLista.agregarArista(indiceDestino, indiceOrigen, peso); // Bidireccional

                grafoMatriz.agregarArista(indiceOrigen, indiceDestino, peso);
                grafoMatriz.agregarArista(indiceDestino, indiceOrigen, peso); // Bidireccional

                aristasAgregadas++;

            } catch (NumberFormatException e) {
                System.err.println("Error parseando número en fila: " + Arrays.toString(fila));
                System.err.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error procesando fila: " + Arrays.toString(fila));
                e.printStackTrace();
            }
        }

        System.out.println("Grafos construidos:");
        System.out.println("  Vértices: " + numeroVertices);
        System.out.println("  Aristas agregadas: " + aristasAgregadas);

        selectorOrigen.getItems().clear();
        selectorDestino.getItems().clear();

        for (int i = 0; i < numeroVertices; i++) {
            selectorOrigen.getItems().add(i);
            selectorDestino.getItems().add(i);
        }

        selectorOrigen.setConverter(new javafx.util.StringConverter<Integer>() {
            @Override
            public String toString(Integer idx) {
                return idx == null ? "" : indiceANombre.get(idx);
            }
            @Override
            public Integer fromString(String string) {
                return null;
            }
        });

        selectorDestino.setConverter(new javafx.util.StringConverter<Integer>() {
            @Override
            public String toString(Integer idx) {
                return idx == null ? "" : indiceANombre.get(idx);
            }
            @Override
            public Integer fromString(String string) {
                return null;
            }
        });
    }

    private void cambiarAlgoritmo() {
        String algoritmoSeleccionado = selectorAlgoritmo.getValue();
        if (algoritmoSeleccionado == null) {
            return;
        }

        panelParametros.setVisible(true);

        selectorOrigen.getSelectionModel().clearSelection();
        selectorDestino.getSelectionModel().clearSelection();
        selectorOrigen.setValue(null);
        selectorDestino.setValue(null);

        areaDetalles.clear();

        if (algoritmoSeleccionado.contains("Dijkstra")) {

            labelOrigen.setVisible(true);
            selectorOrigen.setVisible(true);
            labelDestino.setVisible(true);
            selectorDestino.setVisible(true);

            labelOrigen.setText("Centro de Distribución Origen:");
            labelDestino.setText("Centro de Distribución Destino (Opcional):");

            StringBuilder desc = new StringBuilder();
            if (algoritmoSeleccionado.toLowerCase().contains("recursivo")) {
                desc.append("ALGORITMO DE DIJKSTRA (VERSIÓN RECURSIVA)\n");
            } else {
                desc.append("ALGORITMO DE DIJKSTRA (VERSIÓN ITERATIVA)\n");
            }
            desc.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            desc.append("Calcula la ruta más corta desde un centro origen\n");
            desc.append("hacia todos los demás centros conectados.\n\n");
            desc.append("Parámetros:\n");
            desc.append("• Origen (obligatorio)\n");
            desc.append("• Destino (opcional)\n\n");
            desc.append("Al ejecutar, también se calculará la otra versión de Dijkstra\n");
            desc.append("(iterativa/recursiva) para comparar tiempos de ejecución.");
            areaDetalles.setText(desc.toString());

            botonEjecutarAlgoritmo.setDisable(false);

        } else if (algoritmoSeleccionado.contains("Floyd")) {

            labelOrigen.setVisible(false);
            selectorOrigen.setVisible(false);
            labelDestino.setVisible(false);
            selectorDestino.setVisible(false);

            areaDetalles.setText("""
                ALGORITMO DE FLOYD-WARSHALL
                ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

                Calcula las rutas más cortas entre todos los pares
                de centros en la red.

                No requiere parámetros.
                """);

            botonEjecutarAlgoritmo.setDisable(false);
        }
    }

    private void ejecutarAlgoritmo() {
        String algoritmoSeleccionado = selectorAlgoritmo.getValue();

        if (algoritmoSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un algoritmo");
            return;
        }

        if (algoritmoSeleccionado.contains("Dijkstra")) {
            ejecutarDijkstra();
        } else if (algoritmoSeleccionado.contains("Floyd")) {
            ejecutarFloydWarshall();
        }
    }

    private void ejecutarDijkstra() {
        Integer origen = selectorOrigen.getValue();

        if (origen == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un Centro de Distribución de origen");
            return;
        }

        if (grafoLista == null) {
            mostrarAlerta("Error", "No se ha construido el grafo. Cargue primero un archivo CSV.");
            return;
        }

        String algoritmoSeleccionado = selectorAlgoritmo.getValue();
        boolean usarRecursivo = algoritmoSeleccionado != null &&
                algoritmoSeleccionado.toLowerCase().contains("recursivo");

        System.out.println("Ejecutando Dijkstra desde nodo: " + origen + " (" + indiceANombre.get(origen) + ")");
        System.out.println("Implementación seleccionada para la tabla: " +
                (usarRecursivo ? "recursiva" : "iterativa"));

        Dijkstra dijkstra = new Dijkstra();

        long inicioIter = System.nanoTime();
        ResultadoDijkstra resultadoIter = dijkstra.calcular(grafoLista, origen);
        long finIter = System.nanoTime();

        long inicioRec = System.nanoTime();
        ResultadoDijkstra resultadoRec = dijkstra.calcularRecursivo(grafoLista, origen);
        long finRec = System.nanoTime();

        double tiempoIterMs = (finIter - inicioIter) / 1_000_000.0;
        double tiempoRecMs = (finRec - inicioRec) / 1_000_000.0;

        ResultadoDijkstra resultadoParaTabla = usarRecursivo ? resultadoRec : resultadoIter;

        System.out.println("Dijkstra iterativo: " + tiempoIterMs + " ms");
        System.out.println("Dijkstra recursivo: " + tiempoRecMs + " ms");

        mostrarResultadosDijkstra(resultadoParaTabla, origen, tiempoIterMs, tiempoRecMs, usarRecursivo);
    }

    private void mostrarResultadosDijkstra(ResultadoDijkstra resultado,
                                           int origen,
                                           double tiempoIterMs,
                                           double tiempoRecMs,
                                           boolean usandoRecursivo) {

        tablaResultados.getColumns().clear();
        tablaResultados.getItems().clear();

        TableColumn<String[], String> colDestino = new TableColumn<>("Centro Destino");
        colDestino.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> colDistancia = new TableColumn<>("Distancia Mínima");
        colDistancia.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colRuta = new TableColumn<>("Ruta");
        colRuta.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[2]));

        tablaResultados.getColumns().addAll(colDestino, colDistancia, colRuta);

        List<String[]> datosResultado = new ArrayList<>();

        for (int i = 0; i < numeroVertices; i++) {
            if (i == origen) continue;

            String nombreDestino = indiceANombre.get(i);
            int distancia = resultado.getDistancia(i);
            String distanciaStr = (distancia == Integer.MAX_VALUE) ? "∞" : String.valueOf(distancia);

            List<Integer> ruta = resultado.getRutaMinima(i);
            StringBuilder rutaStr = new StringBuilder();

            if (ruta == null || ruta.isEmpty() || distancia == Integer.MAX_VALUE) {
                rutaStr.append("No hay ruta");
            } else {
                for (int j = 0; j < ruta.size(); j++) {
                    rutaStr.append(indiceANombre.get(ruta.get(j)));
                    if (j < ruta.size() - 1) {
                        rutaStr.append(" → ");
                    }
                }
            }

            datosResultado.add(new String[]{
                    nombreDestino,
                    distanciaStr,
                    rutaStr.toString()
            });
        }

        ObservableList<String[]> datosObservables = FXCollections.observableArrayList(datosResultado);
        tablaResultados.setItems(datosObservables);

        Integer destino = selectorDestino.getValue();
        StringBuilder detalles = new StringBuilder();

        String nombreImplementacion = usandoRecursivo
                ? "ALGORITMO DE DIJKSTRA (VERSIÓN RECURSIVA)"
                : "ALGORITMO DE DIJKSTRA (VERSIÓN ITERATIVA)";

        detalles.append(nombreImplementacion).append("\n");
        detalles.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalles.append("Origen: ").append(indiceANombre.get(origen)).append("\n\n");

        if (destino != null && destino != origen) {
            int dist = resultado.getDistancia(destino);
            List<Integer> rutaMin = resultado.getRutaMinima(destino);

            detalles.append("RUTA ESPECÍFICA HACIA: ").append(indiceANombre.get(destino)).append("\n");
            detalles.append("Distancia: ").append(dist == Integer.MAX_VALUE ? "∞" : dist).append("\n");
            detalles.append("Ruta: ");

            if (rutaMin == null || rutaMin.isEmpty() || dist == Integer.MAX_VALUE) {
                detalles.append("No existe ruta");
            } else {
                for (int i = 0; i < rutaMin.size(); i++) {
                    detalles.append(indiceANombre.get(rutaMin.get(i)));
                    if (i < rutaMin.size() - 1) {
                        detalles.append(" → ");
                    }
                }
            }
            detalles.append("\n\n");
        }

        detalles.append("La tabla muestra las distancias mínimas desde el origen\n");
        detalles.append("hacia todos los demás Centros de Distribución.\n\n");

        detalles.append("COMPARACIÓN DE TIEMPOS DE EJECUCIÓN\n");
        detalles.append("(solo cálculo del algoritmo, en esta ejecución)\n\n");
        detalles.append("• Dijkstra iterativo: ")
                .append(String.format(Locale.US, "%.3f ms", tiempoIterMs))
                .append("\n");
        detalles.append("• Dijkstra recursivo: ")
                .append(String.format(Locale.US, "%.3f ms", tiempoRecMs))
                .append("\n");

        if (tiempoIterMs > 0 && tiempoRecMs > 0) {
            double factor;
            String masLento;
            if (tiempoRecMs >= tiempoIterMs) {
                factor = tiempoRecMs / tiempoIterMs;
                masLento = "recursivo";
            } else {
                factor = tiempoIterMs / tiempoRecMs;
                masLento = "iterativo";
            }
            detalles.append("• En esta ejecución, el algoritmo ")
                    .append(masLento)
                    .append(" fue ")
                    .append(" más lento.\n");
        }

        areaDetalles.setText(detalles.toString());
    }

    private void ejecutarFloydWarshall() {
        System.out.println("Ejecutando Floyd-Warshall");

        int[][] matrizGrafo = new int[numeroVertices][numeroVertices];

        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                matrizGrafo[i][j] = grafoMatriz.getMatriz(i, j);
            }
        }

        FloydWarshall floydWarshall = new FloydWarshall();
        ResultadoFloydWarshall resultado = floydWarshall.calcular(matrizGrafo);

        System.out.println("Floyd-Warshall completado");

        mostrarResultadosFloydWarshall(resultado);
    }

    private void mostrarResultadosFloydWarshall(ResultadoFloydWarshall resultado) {
        tablaResultados.getColumns().clear();

        TableColumn<String[], String> colOrigen = new TableColumn<>("Origen \\ Destino");
        colOrigen.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[0]));

        tablaResultados.getColumns().add(colOrigen);

        for (int j = 0; j < numeroVertices; j++) {
            final int destino = j;
            TableColumn<String[], String> col = new TableColumn<>(indiceANombre.get(j));
            col.setCellValueFactory(data ->
                    new SimpleStringProperty(data.getValue()[destino + 1]));
            tablaResultados.getColumns().add(col);
        }

        List<String[]> datosResultado = new ArrayList<>();
        int[][] distancias = resultado.getDistancia();

        for (int i = 0; i < numeroVertices; i++) {
            String[] fila = new String[numeroVertices + 1];
            fila[0] = indiceANombre.get(i);

            for (int j = 0; j < numeroVertices; j++) {
                if (distancias[i][j] == Integer.MAX_VALUE) {
                    fila[j + 1] = "∞";
                } else if (i == j) {
                    fila[j + 1] = "0";
                } else {
                    fila[j + 1] = String.valueOf(distancias[i][j]);
                }
            }

            datosResultado.add(fila);
        }

        ObservableList<String[]> datosObservables = FXCollections.observableArrayList(datosResultado);
        tablaResultados.setItems(datosObservables);

        StringBuilder detalles = new StringBuilder();
        detalles.append("RESULTADOS - ALGORITMO DE FLOYD-WARSHALL\n");
        detalles.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalles.append("MATRIZ DE DISTANCIAS MÍNIMAS\n\n");
        detalles.append("La tabla muestra las distancias mínimas entre TODOS\n");
        detalles.append("los pares de Centros de Distribución en la red.\n\n");
        detalles.append("• Filas: Centro de origen\n");
        detalles.append("• Columnas: Centro de destino\n");
        detalles.append("• Valores: Distancia/tiempo mínimo\n");
        detalles.append("• ∞: No existe ruta\n");
        detalles.append("• 0: Mismo centro\n\n");

        int rutasExistentes = 0;
        int distanciaTotal = 0;
        int distanciaMax = 0;

        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (i != j && distancias[i][j] != Integer.MAX_VALUE) {
                    rutasExistentes++;
                    distanciaTotal += distancias[i][j];
                    distanciaMax = Math.max(distanciaMax, distancias[i][j]);
                }
            }
        }

        detalles.append("ESTADÍSTICAS:\n");
        detalles.append("• Rutas existentes: ").append(rutasExistentes).append("\n");
        detalles.append("• Distancia promedio: ");
        detalles.append(rutasExistentes > 0 ? (distanciaTotal / rutasExistentes) : 0).append("\n");
        detalles.append("• Distancia máxima: ").append(distanciaMax).append("\n");

        areaDetalles.setText(detalles.toString());
    }

    private void rellenarTabView() {
        tablaDatos.getColumns().clear();

        for (int i = 0; i < encabezadosOriginales.length; i++) {
            final int indice = i;
            TableColumn<String[], String> columna = new TableColumn<>(encabezadosOriginales[i]);
            columna.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            indice < data.getValue().length ? data.getValue()[indice] : ""
                    )
            );
            tablaDatos.getColumns().add(columna);
        }

        ObservableList<String[]> datosTabla = FXCollections.observableArrayList(datosCompletos);
        tablaDatos.setItems(datosTabla);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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

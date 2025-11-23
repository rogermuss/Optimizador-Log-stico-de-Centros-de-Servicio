package EstructurasDeDatos;

import java.util.Arrays;

public class GrafoMatriz {
    private int vertices;
    private int[][] matriz;

    public GrafoMatriz(int vertices) {
        this.vertices = vertices;
        matriz = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            Arrays.fill(matriz[i], Integer.MAX_VALUE); //Se llena con un valor infinito
            matriz[i][i] = 0; //Entre si mismo vale 0
        }
    }

    public void agregarArista(int origen, int destino,int costo) {
            matriz[origen][destino] = costo;
    }

    public int getMatriz(int origen, int destino) {
        return matriz[origen][destino];
    }

    public int getVertices() {
        return vertices;
    }
}

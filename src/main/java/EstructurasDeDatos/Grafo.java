package EstructurasDeDatos;

import Algorithms.NodeComparator;

import java.util.ArrayList;
import java.util.List;

public class Grafo {
    private int Vertices; //cantidad de vertices del grafo
    private List<List<NodeComparator>> listaAdyacencia;

    //Inicializa un grafo de N vertices, contendra los nodos a los que esta conectado junto con los costos
    public Grafo(int vertices) {
        this.Vertices = vertices;
        this.listaAdyacencia = new ArrayList<List<NodeComparator>>();
        for (int i = 0; i < this.Vertices; i++) {
            listaAdyacencia.add(new ArrayList<>());
        }
    }

    public void agregarArista(int origen, int destino, int costo) {
        listaAdyacencia.get(origen).add(new NodeComparator(destino, costo));
    }

    public List<List<NodeComparator>> getListaAdyacencia() {
        return listaAdyacencia;
    }

    public int getVertices() {
        return Vertices;
    }

    public void setVertices(int vertices) {
        this.Vertices = vertices;
    }
}


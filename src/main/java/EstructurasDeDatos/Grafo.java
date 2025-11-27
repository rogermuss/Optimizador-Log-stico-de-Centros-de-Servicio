package EstructurasDeDatos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Grafo ponderado no dirigido, implementado con lista de adyacencia.
public class Grafo {

    private final int numVertices;
    private final List<List<Arista>> adyacencia;

    public Grafo(int numVertices) {
        this.numVertices = numVertices;
        this.adyacencia = new ArrayList<>(numVertices);

        for (int i = 0; i < numVertices; i++) {
            adyacencia.add(new ArrayList<>());
        }
    }

    // Agrega una arista no dirigida entre u y v con cierto peso.
    public void agregarArista(int u, int v, double peso) {
        if (u < 0 || u >= numVertices || v < 0 || v >= numVertices) {
            throw new IllegalArgumentException("Índices de vértice fuera de rango");
        }

        // Se agregan las dos direcciones para que el grafo se comporte como no dirigido.
        Arista a1 = new Arista(u, v, peso);
        Arista a2 = new Arista(v, u, peso);

        adyacencia.get(u).add(a1);
        adyacencia.get(v).add(a2);
    }

    public int getNumVertices() {
        return numVertices;
    }

    // Devuelve la lista de aristas adyacentes a un vértice.
    public List<Arista> getAdyacentes(int vertice) {
        if (vertice < 0 || vertice >= numVertices) {
            return Collections.emptyList();
        }
        return adyacencia.get(vertice);
    }
}

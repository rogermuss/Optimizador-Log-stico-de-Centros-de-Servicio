package Algorithms;

import EstructurasDeDatos.Arista;
import EstructurasDeDatos.Grafo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

// Implementa el algoritmo de Prim para obtener un Árbol de Expansión Mínima (MST).
public class AlgoritmoDePrim {

    // Clase interna para devolver el resultado del algoritmo
    public static class ResultadoPrim {
        private final List<Arista> aristasMST;
        private final double costoTotal;

        public ResultadoPrim(List<Arista> aristasMST, double costoTotal) {
            this.aristasMST = aristasMST;
            this.costoTotal = costoTotal;
        }

        public List<Arista> getAristasMST() {
            return aristasMST;
        }

        public double getCostoTotal() {
            return costoTotal;
        }
    }

    // Calcula el MST usando Prim, partiendo del nodo "inicio".
    public ResultadoPrim calcularMST(Grafo grafo, int inicio) {
        int n = grafo.getNumVertices();
        if (n == 0) {
            return new ResultadoPrim(new ArrayList<>(), 0.0);
        }

        boolean[] visitado = new boolean[n];
        List<Arista> resultado = new ArrayList<>();
        double costoTotal = 0.0;

        // Cola de prioridad ordenada por peso de arista
        PriorityQueue<Arista> cola = new PriorityQueue<>(
                Comparator.comparingDouble(Arista::getPeso)
        );

        // Marcamos el nodo inicial como visitado y agregamos sus aristas
        visitado[inicio] = true;
        for (Arista a : grafo.getAdyacentes(inicio)) {
            cola.offer(a);
        }

        int visitados = 1;

        // Mientras queden vértices por visitar y haya aristas disponibles
        while (!cola.isEmpty() && visitados < n) {
            Arista arista = cola.poll();
            int u = arista.getOrigen();
            int v = arista.getDestino();

            int nuevo = -1;

            // Buscamos una arista que conecte un visitado con un no visitado
            if (visitado[u] && !visitado[v]) {
                nuevo = v;
            } else if (!visitado[u] && visitado[v]) {
                nuevo = u;
            } else {
                // Si ambos extremos ya estaban visitados, o ninguno, esta arista no sirve
                continue;
            }

            // Aceptamos la arista en el MST
            resultado.add(arista);
            costoTotal += arista.getPeso();
            visitado[nuevo] = true;
            visitados++;

            // Agregamos las aristas que salen del nuevo vértice
            for (Arista siguiente : grafo.getAdyacentes(nuevo)) {
                int otro = siguiente.getDestino();
                if (!visitado[otro]) {
                    cola.offer(siguiente);
                }
            }
        }

        // Si visitados < n, el grafo no era completamente conexo y hemos obtenido un bosque mínimo
        return new ResultadoPrim(resultado, costoTotal);
    }
}

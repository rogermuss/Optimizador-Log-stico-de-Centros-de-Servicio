package Algorithms;
import java.util.*;
import EstructurasDeDatos.*;

public class Dijkstra {
    public ResultadoDijkstra calcular(GrafoLista grafo, int origen) {
        int vertices = grafo.getVertices();
        int[] distanciaMin = new int[vertices];
        int[] rutaAnterior = new int[vertices];

        Arrays.fill(distanciaMin, Integer.MAX_VALUE);
        Arrays.fill(rutaAnterior, -1);
        distanciaMin[origen] = 0;

        PriorityQueue<NodeComparator> colaPrioridad =
                new PriorityQueue<>(new NodeComparator());

        colaPrioridad.add(new NodeComparator(origen, 0));

        while (!colaPrioridad.isEmpty()) {
            NodeComparator actual = colaPrioridad.poll();
            int u = actual.node;

            if (actual.cost > distanciaMin[u]) {
                continue;
            }

            for(NodeComparator vecino: grafo.getListaAdyacencia().get(u)){
                int v = vecino.node;
                int costo = vecino.cost;

                if(distanciaMin[u] != Integer.MAX_VALUE &&
                        distanciaMin[u] + costo < distanciaMin[v]) {
                    distanciaMin[v] = distanciaMin[u] + costo;
                    rutaAnterior[v] = u;
                    colaPrioridad.add(new NodeComparator(v, distanciaMin[v]));
                }
            }
        }
        return new ResultadoDijkstra(distanciaMin, rutaAnterior);
    }

    public ResultadoDijkstra calcularRecursivo(GrafoLista grafo, int origen) {
        int vertices = grafo.getVertices();
        int[] distanciaMin = new int[vertices];
        int[] rutaAnterior = new int[vertices];

        Arrays.fill(distanciaMin, Integer.MAX_VALUE);
        Arrays.fill(rutaAnterior, -1);
        distanciaMin[origen] = 0;

        PriorityQueue<NodeComparator> colaPrioridad =
                new PriorityQueue<>(new NodeComparator());

        colaPrioridad.add(new NodeComparator(origen, 0));

        dijkstraRec(colaPrioridad, grafo, distanciaMin, rutaAnterior);

        return new ResultadoDijkstra(distanciaMin, rutaAnterior);
    }

    private void dijkstraRec(PriorityQueue<NodeComparator> colaPrioridad,
                             GrafoLista grafo,
                             int[] distanciaMin,
                             int[] rutaAnterior) {

        if (colaPrioridad.isEmpty()) {
            return;
        }

        NodeComparator actual = colaPrioridad.poll();
        int u = actual.node;

        if (actual.cost > distanciaMin[u]) {
            dijkstraRec(colaPrioridad, grafo, distanciaMin, rutaAnterior);
            return;
        }

        for (NodeComparator vecino : grafo.getListaAdyacencia().get(u)) {
            int v = vecino.node;
            int costo = vecino.cost;

            if (distanciaMin[u] != Integer.MAX_VALUE &&
                    distanciaMin[u] + costo < distanciaMin[v]) {
                distanciaMin[v] = distanciaMin[u] + costo;
                rutaAnterior[v] = u;
                colaPrioridad.add(new NodeComparator(v, distanciaMin[v]));
            }
        }

        dijkstraRec(colaPrioridad, grafo, distanciaMin, rutaAnterior);
    }

}
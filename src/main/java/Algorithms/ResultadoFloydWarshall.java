package Algorithms;

import java.util.List;
import java.util.*;

public class ResultadoFloydWarshall {
    public int[][] distancia;
    public int[][] siguiente;

    public ResultadoFloydWarshall(int[][] distancia, int[][] siguiente) {
            this.distancia = distancia;
            this.siguiente = siguiente;
    }

    public int[][] getDistancia() {
        return distancia;
    }

    public int[][] getSiguiente() {
        return siguiente;
    }

    public List<Integer> getCamino(int u, int v) {
        if(siguiente[u][v] == -1){
            return null;
        }

        List<Integer> camino = new ArrayList<>();
        camino.add(u);

        while(u != v){
            u = siguiente[u][v];
            camino.add(u);
        }
        return camino;
    }
}

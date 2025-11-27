package Algorithms;

import java.util.*;

public class ResultadoDijkstra {
    private int[] distancias;
    private int[] previos;

    public ResultadoDijkstra(int[] distancias, int[] previous){
        this.distancias = distancias;
        this.previos = previous;   // ← CORREGIDO
    }

    public int getDistancia(int nodo){
        return distancias[nodo];
    }

    public int getDistanciaMinima() {
        int minimo = Integer.MAX_VALUE;
        for (int d : distancias) {
            if (d < minimo) minimo = d;
        }
        return minimo;
    }

    public List<Integer> getRutaMinima(int objetivo){
        List<Integer> rutaMinima = new ArrayList<>();

        if (previos == null)
            return rutaMinima;     // ← PROTECCIÓN EXTRA

        int actual = objetivo;

        while (actual != -1){
            rutaMinima.add(0, actual);
            actual = previos[actual];
        }

        return rutaMinima;
    }

    public int[] getDistancias(){
        return distancias;
    }

    public int[] getPrevios(){
        return previos;
    }
}

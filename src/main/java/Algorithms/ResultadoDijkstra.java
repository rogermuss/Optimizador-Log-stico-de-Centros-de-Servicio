package Algorithms;

import java.util.*;
public class ResultadoDijkstra {
    private int[] distancias;
    private int[] previos;

    public ResultadoDijkstra(int[] distancias, int[] previous){
        this.distancias = distancias;
        this.previos = previos;
    }

    public int getDistancia(int nodo){
        return distancias[nodo];
    }

    public int getDistanciaMinima() {
        int minimo = Integer.MAX_VALUE;
        for (int i = 0; i < distancias.length; i++) {
            if (distancias[i] < minimo) {
                minimo = distancias[i];
            }
        }
        return minimo;
    }

    public List<Integer> getRutaMinima(int objetivo){
            List<Integer> rutaMinima = new ArrayList<>();
            int actual = objetivo;
            while (actual != -1){
                rutaMinima.add(0, actual);
                actual = previos[actual];
            }
            if (rutaMinima.size() == 1  && rutaMinima.get(0) != objetivo){
                return new ArrayList<>();
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

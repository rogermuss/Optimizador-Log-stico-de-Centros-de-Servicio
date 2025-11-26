package Algorithms;

public class FloydWarshall {

    public ResultadoFloydWarshall calcular(int[][] grafo) {

        int cantidad = grafo.length;
        int[][] distancias = new int[cantidad][cantidad];
        int[][] siguiente = new int[cantidad][cantidad];

        for (int i = 0; i < cantidad; i++) {
            for (int j = 0; j < cantidad; j++) {

                distancias[i][j] = grafo[i][j];

                if (i == j || grafo[i][j] == Integer.MAX_VALUE) {
                    siguiente[i][j] = -1;
                } else {
                    siguiente[i][j] = j;
                }
            }
        }

        // ALGORITMO Floyd–Warshall
        for (int k = 0; k < cantidad; k++) {
            for (int i = 0; i < cantidad; i++) {
                for (int j = 0; j < cantidad; j++) {
                    if (distancias[i][k] == Integer.MAX_VALUE ||
                            distancias[k][j] == Integer.MAX_VALUE) {
                        continue;
                    }

                    int nuevoCosto = distancias[i][k] + distancias[k][j];

                    if (nuevoCosto < distancias[i][j]) {
                        distancias[i][j] = nuevoCosto;
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }

        return new ResultadoFloydWarshall(distancias, siguiente);
    }
}

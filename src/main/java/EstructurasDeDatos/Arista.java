package EstructurasDeDatos;

public class Arista {

    private final int origen;
    private final int destino;
    private final double peso;

    public Arista(int origen, int destino, double peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return "Arista{" +
                "origen=" + origen +
                ", destino=" + destino +
                ", peso=" + peso +
                '}';
    }
}

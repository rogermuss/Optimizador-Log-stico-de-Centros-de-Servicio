package Data;

public class Producto {
    private String id;
    private String nombre;
    private int stock;
    private String ubicacion; // CAMBIO: en lugar de precio

    public Producto(String id, String nombre, int stock, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStock() {
        return stock;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                " | Producto: " + nombre +
                " | Stock: " + stock +
                " | Ubicación: " + ubicacion;
    }
}
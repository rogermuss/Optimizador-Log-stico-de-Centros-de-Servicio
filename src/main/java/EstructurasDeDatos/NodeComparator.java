package EstructurasDeDatos;
import java.util.Comparator;

//Nodo con compatarator referencia:
//https://www.geeksforgeeks.org/java/dijkstras-shortest-path-algorithm-in-java-using-priorityqueue/
public class NodeComparator implements Comparator<NodeComparator> {
    public int node;
    public int cost;

    public NodeComparator() {}

    public NodeComparator(int nodo, int costo) {
        this.node = nodo;
        this.cost = costo;
    }

    @Override
    public int compare(NodeComparator node1, NodeComparator node2) {
        return Integer.compare(node1.cost, node2.cost);
    }
}

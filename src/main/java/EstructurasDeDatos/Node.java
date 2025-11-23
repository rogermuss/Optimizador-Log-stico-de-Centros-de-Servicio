package EstructurasDeDatos;

public class Node<T> {
    private T item;
    private int height;
    private Node<T> left, right;

    public Node(T item) {
        this.item = item;
        height = 1;
    }

    public T getItem() { return item; }
    public void setItem(T item) { this.item = item; }

    public Node<T> getLeft() { return left; }
    public void setLeft(Node<T> left) { this.left = left; }

    public Node<T> getRight() { return right; }
    public void setRight(Node<T> right) { this.right = right; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
}


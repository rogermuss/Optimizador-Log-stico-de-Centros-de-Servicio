package EstructurasDeDatos;
//Referencia de algoritmo
//Árbol AVL. (n.d.). https://www.programiz.com/dsa/avl-tree

import java.util.Comparator;

public class ArbolAVL<T> {
    private Node<T> root;
    private Comparator<T> comparator;

    public ArbolAVL(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public Node<T> getRoot() {
        return root;
    }

    public void setRoot(Node<T> root) {
        this.root = root;
    }

    public int altura(Node<T> N) {
        if (N == null)
            return 0;
        return N.getHeight();
    }

    public int maximo(int a, int b) {
        return (a > b) ? a : b;
    }

    public Node<T> rotarDerecha(Node<T> y) {
        Node<T> x = y.getLeft();
        Node<T> T2 = x.getRight();

        x.setRight(y);
        y.setLeft(T2);

        y.setHeight(maximo(altura(y.getLeft()), altura(y.getRight())) + 1);
        x.setHeight(maximo(altura(x.getLeft()), altura(x.getRight())) + 1);

        return x;
    }

    public Node<T> rotarIzquierda(Node<T> x) {
        Node<T> y = x.getRight();
        Node<T> T2 = y.getLeft();

        y.setLeft(x);
        x.setRight(T2);

        x.setHeight(maximo(altura(x.getLeft()), altura(x.getRight())) + 1);
        y.setHeight(maximo(altura(y.getLeft()), altura(y.getRight())) + 1);

        return y;
    }

    public int obtenerFactorBalance(Node<T> N) {
        if (N == null)
            return 0;
        return altura(N.getLeft()) - altura(N.getRight());
    }

    public Node<T> insertarNodo(Node<T> nodo, T item) {

        if (nodo == null)
            return (new Node<>(item));

        if (comparator.compare(item,nodo.getItem()) < 0)
            nodo.setLeft(insertarNodo(nodo.getLeft(), item));
        else if (comparator.compare(item, nodo.getItem()) > 0)
            nodo.setRight(insertarNodo(nodo.getRight(), item));
        else
            return nodo;

        nodo.setHeight(1 + maximo(altura(nodo.getLeft()), altura(nodo.getRight())));
        int factorBalance = obtenerFactorBalance(nodo);

        if (factorBalance > 1) {
            if (comparator.compare(item, nodo.getLeft().getItem()) < 0) {
                return rotarDerecha(nodo);
            } else if (comparator.compare(item, nodo.getLeft().getItem()) > 0) {
                nodo.setLeft(rotarIzquierda(nodo.getLeft()));
                return rotarDerecha(nodo);
            }
        }

        if (factorBalance < -1) {
            if (comparator.compare(item,nodo.getRight().getItem()) > 0) {
                return rotarIzquierda(nodo);
            } else if (comparator.compare(item,nodo.getRight().getItem()) < 0) {
                nodo.setRight(rotarDerecha(nodo.getRight()));
                return rotarIzquierda(nodo);
            }
        }

        return nodo;
    }
}
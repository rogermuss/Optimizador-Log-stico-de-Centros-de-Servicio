package EstructurasDeDatos;
//Referencia de algoritmo
//Árbol AVL. (n.d.). https://www.programiz.com/dsa/avl-tree

import java.util.Comparator;

class ArbolAVL<T> {
    private Node<T> root;
    private Comparator<T> comparator;

    ArbolAVL(Comparator<T> comparator) {
        this.comparator = comparator;
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

    public Node<T> nodoConValorMinimo(Node<T> nodo) {
        Node<T> actual = nodo;
        while (actual.getLeft() != null)
            actual = actual.getLeft();
        return actual;
    }

    public Node<T> eliminarNodo(Node<T> raiz, T item) {

        if (raiz == null)
            return raiz;

        if (comparator.compare(item, raiz.getItem()) < 0)
            raiz.setLeft(eliminarNodo(raiz.getLeft(), item));
        else if (comparator.compare(item, raiz.getItem()) > 0)
            raiz.setRight(eliminarNodo(raiz.getRight(), item));
        else {
            if ((raiz.getLeft() == null) || (raiz.getRight() == null)) {
                Node<T> temp;

                if (raiz.getLeft() != null)
                    temp = raiz.getLeft();
                else
                    temp = raiz.getRight();

                if (temp == null) {
                    raiz = null;
                } else
                    raiz = temp;
            } else {
                Node<T> temp = nodoConValorMinimo(raiz.getRight());
                raiz.setItem(temp.getItem());
                raiz.setRight(eliminarNodo(raiz.getRight(), temp.getItem()));
            }
        }

        if (raiz == null)
            return raiz;

        raiz.setHeight(maximo(altura(raiz.getLeft()), altura(raiz.getRight())) + 1);
        int factorBalance = obtenerFactorBalance(raiz);

        if (factorBalance > 1) {
            if (obtenerFactorBalance(raiz.getLeft()) >= 0) {
                return rotarDerecha(raiz);
            } else {
                raiz.setLeft(rotarIzquierda(raiz.getLeft()));
                return rotarDerecha(raiz);
            }
        }

        if (factorBalance < -1) {
            if (obtenerFactorBalance(raiz.getRight()) <= 0) {
                return rotarIzquierda(raiz);
            } else {
                raiz.setRight(rotarDerecha(raiz.getRight()));
                return rotarIzquierda(raiz);
            }
        }

        return raiz;
    }

    public void preOrden(Node<T> nodo) {
        if (nodo != null) {
            System.out.print(nodo.getItem() + " ");
            preOrden(nodo.getLeft());
            preOrden(nodo.getRight());
        }
    }

    public void inOrden(Node<T> nodo) {
        if (nodo != null) {
            inOrden(nodo.getLeft());
            System.out.print(nodo.getItem() + " ");
            inOrden(nodo.getRight());
        }
    }

    public void postOrden(Node<T> nodo) {
        if (nodo != null) {
            postOrden(nodo.getLeft());
            postOrden(nodo.getRight());
            System.out.print(nodo.getItem() + " ");
        }
    }

    public void imprimirArbol(Node<T> actual, String indentacion, boolean ultimo) {
        if (actual != null) {
            System.out.print(indentacion);
            if (ultimo) {
                System.out.print("R----");
                indentacion += "   ";
            } else {
                System.out.print("L----");
                indentacion += "|  ";
            }
            System.out.println(actual.getItem());
            imprimirArbol(actual.getLeft(), indentacion, false);
            imprimirArbol(actual.getRight(), indentacion, true);
        }
    }
}

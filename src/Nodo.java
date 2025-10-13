// Archivo: src/Nodo.java

// Interfaz base para todos los nodos del AST
public interface Nodo {
    <T> T accept(Visitor<T> visitor);
}
// Archivo: src/Visitor.java
public interface Visitor<T> {
    T visit(Programa programa);
    T visit(NodoBloque nodo);
    T visit(NodoDeclaracion nodo);
    T visit(NodoAsignacion nodo);
    T visit(NodoIf nodo);
    T visit(NodoWhile nodo);
    T visit(NodoFor nodo);
    T visit(NodoImprimir nodo);
    T visit(NodoExpresionBinaria nodo);
    T visit(NodoNumero nodo);
    T visit(NodoVariable nodo);
    T visit(NodoStringLiteral nodo); // <--- AÑADIDO
}
// Archivo: src/NodoExpresionBinaria.java

public class NodoExpresionBinaria implements Nodo {
    public final Nodo izquierda;
    public final Token operador;
    public final Nodo derecha;

    public NodoExpresionBinaria(Nodo izquierda, Token operador, Nodo derecha) {
        this.izquierda = izquierda;
        this.operador = operador;
        this.derecha = derecha;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
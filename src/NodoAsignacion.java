// Archivo: src/NodoAsignacion.java
public class NodoAsignacion implements Nodo {
    public final Token identificador;
    public final Nodo expresion;

    public NodoAsignacion(Token identificador, Nodo expresion) {
        this.identificador = identificador;
        this.expresion = expresion;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
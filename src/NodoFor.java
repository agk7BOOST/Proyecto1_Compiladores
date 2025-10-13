// Archivo: src/NodoFor.java
public class NodoFor implements Nodo {
    public final Nodo inicializador;
    public final Nodo condicion;
    public final Nodo incremento;
    public final Nodo cuerpo;

    public NodoFor(Nodo inicializador, Nodo condicion, Nodo incremento, Nodo cuerpo) {
        this.inicializador = inicializador;
        this.condicion = condicion;
        this.incremento = incremento;
        this.cuerpo = cuerpo;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
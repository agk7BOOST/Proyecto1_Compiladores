// Archivo: src/NodoWhile.java
public class NodoWhile implements Nodo {
    public final Nodo condicion;
    public final Nodo cuerpo;

    public NodoWhile(Nodo condicion, Nodo cuerpo) {
        this.condicion = condicion;
        this.cuerpo = cuerpo;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
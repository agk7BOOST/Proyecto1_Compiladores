public class NodoIf implements Nodo {
    public final Nodo condicion;
    public final Nodo cuerpoIf;
    public final Nodo cuerpoElse; // Puede ser null

    public NodoIf(Nodo condicion, Nodo cuerpoIf, Nodo cuerpoElse) {
        this.condicion = condicion;
        this.cuerpoIf = cuerpoIf;
        this.cuerpoElse = cuerpoElse;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
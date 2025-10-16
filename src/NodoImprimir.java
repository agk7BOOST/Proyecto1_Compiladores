public class NodoImprimir implements Nodo {
    public final Nodo expresion;

    public NodoImprimir(Nodo expresion) {
        this.expresion = expresion;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
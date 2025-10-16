public class NodoVariable implements Nodo {
    public final Token nombre;
    public NodoVariable(Token nombre) { this.nombre = nombre; }
    @Override public <T> T accept(Visitor<T> v) { return v.visit(this); }
}
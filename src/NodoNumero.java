public class NodoNumero implements Nodo {
    public final Token valor;
    public NodoNumero(Token valor) { this.valor = valor; }
    @Override public <T> T accept(Visitor<T> v) { return v.visit(this); }
}
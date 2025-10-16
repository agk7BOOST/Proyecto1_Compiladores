public class NodoStringLiteral implements Nodo {
    public final Token valor;

    public NodoStringLiteral(Token valor) {
        this.valor = valor;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
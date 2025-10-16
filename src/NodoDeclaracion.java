public class NodoDeclaracion implements Nodo {
    public final Token tipo;
    public final Token identificador;
    public final Nodo expresion;

    public NodoDeclaracion(Token tipo, Token identificador, Nodo expresion) {
        this.tipo = tipo;
        this.identificador = identificador;
        this.expresion = expresion;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
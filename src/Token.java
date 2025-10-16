public class Token {
    private final TipoToken tipo;
    private final String valor;
    private final int linea;

    private Token(TipoToken tipo, String valor, int linea) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
    }

    public TipoToken getTipo() { return tipo; }
    public String getValor() { return valor; }
    public int getLinea() { return linea; }

    @Override
    public String toString() {
        return String.format("<%s, \"%s\", Linea %d>", tipo, valor, linea);
    }

    public static class Builder {
        private TipoToken tipo;
        private String valor;
        private int linea;

        public Builder conTipo(TipoToken tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder conValor(String valor) {
            this.valor = valor;
            return this;
        }

        public Builder enLinea(int linea) {
            this.linea = linea;
            return this;
        }

        public Token construir() {
            return new Token(this.tipo, this.valor, this.linea);
        }
    }
}
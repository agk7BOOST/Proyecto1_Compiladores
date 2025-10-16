import java.util.List;

public class AnalizadorSemantico {

    private final List<Token> tokens;
    private final TablaSimbolos tablaSimbolos;
    private int posicion;

    public AnalizadorSemantico(List<Token> tokens) {
        this.tokens = tokens;
        this.tablaSimbolos = new TablaSimbolos();
        this.posicion = 0;
    }

    public String analizar() {
        while (hayMasTokens()) {
            Token actual = tokenActual();

            if (actual.getTipo() == TipoToken.PALABRA_CLAVE && esTipo(actual)) {
                analizarDeclaracion();
            }
            else if (actual.getTipo() == TipoToken.IDENTIFICADOR) {
                analizarUsoVariable();
            } else {
                avanzar();
            }
        }
        return tablaSimbolos.getResultados();
    }

    private void analizarDeclaracion() {
        Token tipoToken = tokenActual();
        Token idToken = siguienteToken();

        if (idToken != null && idToken.getTipo() == TipoToken.IDENTIFICADOR) {
            tablaSimbolos.declarar(idToken.getValor(), tipoToken.getTipo());

            avanzar(2);

            if (tokenActual() != null && tokenActual().getValor().equals("=")) {
                avanzar();
                verificarTiposAsignacion();
            }
        } else {
            avanzar();
        }
    }

    private void analizarUsoVariable() {
        tablaSimbolos.buscar(tokenActual().getValor());
        avanzar();
    }

    private void verificarTiposAsignacion() {
        Token valorToken = tokenActual();
        if (valorToken == null) return;

        if (valorToken.getTipo() == TipoToken.IDENTIFICADOR) {
            tablaSimbolos.buscar(valorToken.getValor());
        }
    }

    private Token tokenActual() {
        return posicion < tokens.size() ? tokens.get(posicion) : null;
    }

    private Token siguienteToken() {
        return posicion + 1 < tokens.size() ? tokens.get(posicion + 1) : null;
    }

    private void avanzar() { avanzar(1); }
    private void avanzar(int cantidad) { posicion += cantidad; }
    private boolean hayMasTokens() { return posicion < tokens.size(); }
    private boolean esTipo(Token token) {
        String valor = token.getValor();
        return valor.equals("int") || valor.equals("float") || valor.equals("void");
    }
}
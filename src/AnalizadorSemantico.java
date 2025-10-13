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

            // Si es una palabra clave de tipo (int, float, etc.), es una declaración
            if (actual.getTipo() == TipoToken.PALABRA_CLAVE && esTipo(actual)) {
                analizarDeclaracion();
            }
            // Si es un identificador, puede ser una asignación o parte de otra expresión
            else if (actual.getTipo() == TipoToken.IDENTIFICADOR) {
                analizarUsoVariable();
            } else {
                avanzar(); // Ignoramos otros tokens como operadores, delimitadores, etc.
            }
        }
        return tablaSimbolos.getResultados();
    }

    private void analizarDeclaracion() {
        Token tipoToken = tokenActual();
        Token idToken = siguienteToken();

        if (idToken != null && idToken.getTipo() == TipoToken.IDENTIFICADOR) {
            // Se declara la variable en la tabla de símbolos
            tablaSimbolos.declarar(idToken.getValor(), tipoToken.getTipo());

            // Avanzamos más allá del tipo y el identificador
            avanzar(2);

            // Si hay una asignación, verificamos los tipos
            if (tokenActual() != null && tokenActual().getValor().equals("=")) {
                avanzar(); // Avanzamos sobre el '='
                verificarTiposAsignacion();
            }
        } else {
            avanzar();
        }
    }

    private void analizarUsoVariable() {
        // Al usar una variable (ej. en una asignación), verificamos si fue declarada
        tablaSimbolos.buscar(tokenActual().getValor());
        avanzar();
    }

    private void verificarTiposAsignacion() {
        Token valorToken = tokenActual();
        if (valorToken == null) return;

        // Si el valor es un identificador, verificamos que exista
        if (valorToken.getTipo() == TipoToken.IDENTIFICADOR) {
            tablaSimbolos.buscar(valorToken.getValor());
        }
        // Si es un número, es compatible por defecto (en este lenguaje simple)
        // Si no, sería un error de tipo, pero lo mantenemos simple por ahora.
    }

    // --- Métodos de Ayuda ---
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
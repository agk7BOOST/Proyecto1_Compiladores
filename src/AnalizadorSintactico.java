import java.util.ArrayList;
import java.util.List;

public class AnalizadorSintactico {
    private final List<Token> tokens;
    private int posicion = 0;

    public AnalizadorSintactico(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Programa analizar() {
        List<Nodo> sentencias = new ArrayList<>();
        while (hayMasTokens()) {
            sentencias.add(parseDeclaracionOSentencia());
        }
        return new Programa(sentencias);
    }


    private Nodo parseDeclaracionOSentencia() {
        if (matchValor("int", "float") && siguienteEs(TipoToken.IDENTIFICADOR)) {
            return parseDeclaracion();
        }
        return parseSentencia();
    }

    private Nodo parseSentencia() {
        if (matchValor("if")) return parseIf();
        if (matchValor("while")) return parseWhile();
        if (matchValor("for")) return parseFor();
        if (matchValor("imprimir")) return parseSentenciaImprimir();
        if (matchValor("{")) return new NodoBloque(parseBloque());
        return parseSentenciaDeExpresion();
    }


    private List<Nodo> parseBloque() {
        consumir("{", "Se esperaba '{' para iniciar un bloque.");
        List<Nodo> sentencias = new ArrayList<>();
        while (!matchValor("}") && hayMasTokens()) {
            sentencias.add(parseDeclaracionOSentencia());
        }
        consumir("}", "Se esperaba '}' para cerrar un bloque.");
        return sentencias;
    }

    private Nodo parseDeclaracion() {
        Token tipo = avanzar();
        Token id = consumir(TipoToken.IDENTIFICADOR, "Se esperaba un nombre de variable.");
        Nodo valor = null;
        if (matchValor("=")) {
            consumir("=", "");
            valor = parseExpresion();
        }
        consumir(";", "Las declaraciones deben terminar con ';'.");
        return new NodoDeclaracion(tipo, id, valor);
    }

    private Nodo parseSentenciaDeExpresion() {
        Nodo expr = parseExpresion();
        consumir(";", "Se esperaba ';' al final de la sentencia.");
        return expr;
    }

    private Nodo parseSentenciaImprimir() {
        consumir("imprimir", "");
        consumir("(", "Se esperaba '(' después de 'imprimir'.");
        Nodo expr = parseExpresion();
        consumir(")", "Se esperaba ')' después de la expresión.");
        consumir(";", "La sentencia 'imprimir' debe terminar con ';'.");
        return new NodoImprimir(expr);
    }

    private Nodo parseIf() {
        consumir("if", "");
        consumir("(", "Se esperaba '(' después de 'if'.");
        Nodo condicion = parseExpresion();
        consumir(")", "Se esperaba ')' después de la condición.");
        Nodo cuerpoIf = parseSentencia();
        Nodo cuerpoElse = null;
        if (matchValor("else")) {
            consumir("else", "");
            cuerpoElse = parseSentencia();
        }
        return new NodoIf(condicion, cuerpoIf, cuerpoElse);
    }

    private Nodo parseWhile() {
        consumir("while", "");
        consumir("(", "Se esperaba '(' después de 'while'.");
        Nodo condicion = parseExpresion();
        consumir(")", "Se esperaba ')' después de la condición.");
        Nodo cuerpo = parseSentencia();
        return new NodoWhile(condicion, cuerpo);
    }

    // MÉTODO CORREGIDO
    private Nodo parseFor() {
        consumir("for", "");
        consumir("(", "Se esperaba '(' después de 'for'.");

        Nodo inicializador;
        if (matchValor(";")) {
            inicializador = null;
        } else if (matchValor("int", "float")) {
            Token tipo = avanzar();
            Token id = consumir(TipoToken.IDENTIFICADOR, "Se esperaba un nombre de variable.");
            Nodo valor = null;
            if (matchValor("=")) {
                consumir("=", "");
                valor = parseExpresion();
            }
            inicializador = new NodoDeclaracion(tipo, id, valor);
        } else {
            inicializador = parseExpresion();
        }
        consumir(";", "Se esperaba ';' después del inicializador del for.");

        Nodo condicion = null;
        if (!matchValor(";")) {
            condicion = parseExpresion();
        }
        consumir(";", "Se esperaba ';' después de la condición del for.");

        Nodo incremento = null;
        if (!matchValor(")")) {
            incremento = parseExpresion();
        }
        consumir(")", "Se esperaba ')' para cerrar la cabecera del for.");

        Nodo cuerpo = parseSentencia();
        return new NodoFor(inicializador, condicion, incremento, cuerpo);
    }


    private Nodo parseExpresion() {
        return parseAsignacion();
    }

    private Nodo parseAsignacion() {
        Nodo expr = parseComparacion();
        if (matchValor("=")) {
            avanzar();
            Nodo valor = parseAsignacion();
            if (expr instanceof NodoVariable) {
                Token nombre = ((NodoVariable) expr).nombre;
                return new NodoAsignacion(nombre, valor);
            }
            throw new RuntimeException("Error de Sintaxis: El lado izquierdo de una asignación debe ser una variable.");
        }
        return expr;
    }

    private Nodo parseComparacion() {
        Nodo expr = parseSuma();
        while (matchValor("<", ">", "<=", ">=", "==", "!=")) {
            Token op = avanzar();
            Nodo der = parseSuma();
            expr = new NodoExpresionBinaria(expr, op, der);
        }
        return expr;
    }

    private Nodo parseSuma() {
        Nodo expr = parseFactor();
        while (matchValor("+", "-")) {
            Token op = avanzar();
            Nodo der = parseFactor();
            expr = new NodoExpresionBinaria(expr, op, der);
        }
        return expr;
    }

    private Nodo parseMultiplicacion() {
        Nodo expr = parsePrimario();
        while (matchValor("*", "/")) {
            Token op = avanzar();
            Nodo der = parsePrimario();
            expr = new NodoExpresionBinaria(expr, op, der);
        }
        return expr;
    }

    private Nodo parseFactor() {
        return parseMultiplicacion();
    }


    private Nodo parsePrimario() {
        if (match(TipoToken.STRING)) return new NodoStringLiteral(avanzar());
        if (match(TipoToken.NUMERO)) return new NodoNumero(avanzar());
        if (match(TipoToken.IDENTIFICADOR)) return new NodoVariable(avanzar());
        if (matchValor("(")) {
            consumir("(", "");
            Nodo expr = parseExpresion();
            consumir(")", "Se esperaba ')' después de la expresión.");
            return expr;
        }
        throw new RuntimeException("Expresión no válida encontrada cerca de '" + tokenActual().getValor() + "'");
    }

    private Token avanzar() { return tokens.get(posicion++); }
    private boolean hayMasTokens() { return posicion < tokens.size(); }
    private Token tokenActual() { return hayMasTokens() ? tokens.get(posicion) : null; }
    private boolean match(TipoToken tipo) { return hayMasTokens() && tokenActual().getTipo() == tipo; }
    private boolean matchValor(String... valores) {
        if (!hayMasTokens()) return false;
        for (String valor : valores) if (tokenActual().getValor().equals(valor)) return true;
        return false;
    }
    private boolean siguienteEs(TipoToken tipo) {
        if (!hayMasTokens() || posicion + 1 >= tokens.size()) return false;
        return tokens.get(posicion + 1).getTipo() == tipo;
    }
    private Token consumir(TipoToken tipo, String errorMsg) {
        if (match(tipo)) return avanzar();
        throw new RuntimeException("Error de sintaxis: " + errorMsg);
    }
    private void consumir(String valor, String errorMsg) {
        if (matchValor(valor)) { avanzar(); return; }
        throw new RuntimeException("Error de sintaxis: " + errorMsg + " Se encontró '" + (tokenActual() != null ? tokenActual().getValor() : "EOF") + "'.");
    }
}
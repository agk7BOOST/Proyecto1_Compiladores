
public class AstPrinter implements Visitor<String> {

    private int indentacion = 0;

    public String print(Nodo nodo) {
        return nodo.accept(this);
    }

    private String parenthesize(String name, Nodo... nodos) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        indentacion++;
        for (Nodo n : nodos) {
            if (n != null) {
                sb.append(getIndentacion()).append("|- ").append(n.accept(this));
            }
        }
        indentacion--;
        return sb.toString();
    }

    private String parenthesize(String name, Token token) {
        return name + " (" + token.getValor() + ")\n";
    }

    private String getIndentacion() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < indentacion; i++) {
            sb.append("|   ");
        }
        return sb.toString();
    }

    @Override
    public String visit(Programa programa) {
        StringBuilder sb = new StringBuilder();
        sb.append("Programa\n");
        indentacion++;
        for (Nodo sentencia : programa.sentencias) {
            sb.append(getIndentacion()).append("|- ").append(sentencia.accept(this));
        }
        indentacion--;
        return sb.toString();
    }

    @Override
    public String visit(NodoBloque nodo) {
        return parenthesize("Bloque", nodo.sentencias.toArray(new Nodo[0]));
    }

    @Override
    public String visit(NodoDeclaracion nodo) {
        return parenthesize("Declaracion: " + nodo.identificador.getValor(), nodo.expresion);
    }

    @Override
    public String visit(NodoAsignacion nodo) {
        return parenthesize("Asignacion: " + nodo.identificador.getValor(), nodo.expresion);
    }

    @Override
    public String visit(NodoIf nodo) {
        if (nodo.cuerpoElse == null) {
            return parenthesize("If", nodo.condicion, nodo.cuerpoIf);
        }
        return parenthesize("If-Else", nodo.condicion, nodo.cuerpoIf, nodo.cuerpoElse);
    }

    @Override
    public String visit(NodoWhile nodo) {
        return parenthesize("While", nodo.condicion, nodo.cuerpo);
    }

    @Override
    public String visit(NodoFor nodo) {
        return parenthesize("For", nodo.inicializador, nodo.condicion, nodo.incremento, nodo.cuerpo);
    }

    @Override
    public String visit(NodoImprimir nodo) {
        return parenthesize("Imprimir", nodo.expresion);
    }

    @Override
    public String visit(NodoExpresionBinaria nodo) {
        return parenthesize("Op: " + nodo.operador.getValor(), nodo.izquierda, nodo.derecha);
    }

    @Override
    public String visit(NodoNumero nodo) {
        return parenthesize("Numero", nodo.valor);
    }

    @Override
    public String visit(NodoVariable nodo) {
        return parenthesize("Variable", nodo.nombre);
    }
    @Override
    public String visit(NodoStringLiteral nodo) {
        return parenthesize("String", nodo.valor);
    }
}
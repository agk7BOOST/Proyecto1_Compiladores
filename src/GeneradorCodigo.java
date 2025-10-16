
public class GeneradorCodigo implements Visitor<String> {

    private int indentacion = 1; // Para formatear el código C++

    private String getIndentacion() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentacion; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }

    public String generar(Programa programa) {
        StringBuilder codigo = new StringBuilder();
        codigo.append("#include <iostream>\n");
        codigo.append("#include <string>\n\n");
        codigo.append("int main() {\n");
        codigo.append(programa.accept(this));
        codigo.append(getIndentacion()).append("return 0;\n");
        codigo.append("}\n");
        return codigo.toString();
    }

    @Override
    public String visit(Programa programa) {
        StringBuilder sb = new StringBuilder();
        for (Nodo sentencia : programa.sentencias) {
            sb.append(getIndentacion());
            sb.append(sentencia.accept(this));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String visit(NodoBloque nodo) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        indentacion++;
        for (Nodo sentencia : nodo.sentencias) {
            sb.append(getIndentacion());
            sb.append(sentencia.accept(this));
            sb.append("\n");
        }
        indentacion--;
        sb.append(getIndentacion()).append("}");
        return sb.toString();
    }

    @Override
    public String visit(NodoDeclaracion nodo) {
        return String.format("%s %s = %s;",
                nodo.tipo.getValor(),
                nodo.identificador.getValor(),
                nodo.expresion.accept(this));
    }

    @Override
    public String visit(NodoAsignacion nodo) {
        return String.format("%s = %s;",
                nodo.identificador.getValor(),
                nodo.expresion.accept(this));
    }

    @Override
    public String visit(NodoIf nodo) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("if (%s) ", nodo.condicion.accept(this)));
        sb.append(nodo.cuerpoIf.accept(this));
        if (nodo.cuerpoElse != null) {
            if (!(nodo.cuerpoIf instanceof NodoBloque)) {
                sb.append("\n").append(getIndentacion());
            }
            sb.append(" else ");
            sb.append(nodo.cuerpoElse.accept(this));
        }
        return sb.toString();
    }

    @Override
    public String visit(NodoWhile nodo) {
        return String.format("while (%s) %s",
                nodo.condicion.accept(this),
                nodo.cuerpo.accept(this));
    }

    @Override
    public String visit(NodoFor nodo) {
        String inicializador = (nodo.inicializador != null) ? nodo.inicializador.accept(this).replace(";", "") : "";
        String condicion = (nodo.condicion != null) ? nodo.condicion.accept(this) : "";
        String incremento = (nodo.incremento != null) ? nodo.incremento.accept(this).replace(";", "") : "";

        return String.format("for (%s; %s; %s) %s",
                inicializador,
                condicion,
                incremento,
                nodo.cuerpo.accept(this));
    }

    @Override
    public String visit(NodoImprimir nodo) {
        return String.format("std::cout << %s << std::endl;", nodo.expresion.accept(this));
    }

    @Override
    public String visit(NodoExpresionBinaria nodo) {
        return String.format("(%s %s %s)",
                nodo.izquierda.accept(this),
                nodo.operador.getValor(),
                nodo.derecha.accept(this));
    }

    @Override
    public String visit(NodoNumero nodo) {
        return nodo.valor.getValor();
    }

    @Override
    public String visit(NodoVariable nodo) {
        return nodo.nombre.getValor();
    }

    @Override
    public String visit(NodoStringLiteral nodo) {
        return nodo.valor.getValor();
    }
}
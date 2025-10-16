public class OptimizadorCodigo implements Visitor<Nodo> {

    public Nodo optimizar(Nodo nodo) {
        return nodo.accept(this);
    }

    @Override
    public Nodo visit(Programa programa) {
        programa.sentencias.replaceAll(this::optimizar);
        return programa;
    }

    @Override
    public Nodo visit(NodoBloque nodo) {
        nodo.sentencias.replaceAll(this::optimizar);
        return nodo;
    }

    @Override
    public Nodo visit(NodoDeclaracion nodo) {
        Nodo expresionOptimizada = optimizar(nodo.expresion);
        return new NodoDeclaracion(nodo.tipo, nodo.identificador, expresionOptimizada);
    }

    @Override
    public Nodo visit(NodoAsignacion nodo) {
        Nodo expresionOptimizada = optimizar(nodo.expresion);
        return new NodoAsignacion(nodo.identificador, expresionOptimizada);
    }

    @Override
    public Nodo visit(NodoIf nodo) {
        Nodo condOptimizada = optimizar(nodo.condicion);
        Nodo ifOptimizado = optimizar(nodo.cuerpoIf);
        Nodo elseOptimizado = (nodo.cuerpoElse != null) ? optimizar(nodo.cuerpoElse) : null;
        return new NodoIf(condOptimizada, ifOptimizado, elseOptimizado);
    }

    @Override
    public Nodo visit(NodoWhile nodo) {
        Nodo condOptimizada = optimizar(nodo.condicion);
        Nodo cuerpoOptimizada = optimizar(nodo.cuerpo);
        return new NodoWhile(condOptimizada, cuerpoOptimizada);
    }

    @Override
    public Nodo visit(NodoFor nodo) {
        Nodo initOpt = (nodo.inicializador != null) ? optimizar(nodo.inicializador) : null;
        Nodo condOpt = (nodo.condicion != null) ? optimizar(nodo.condicion) : null;
        Nodo incOpt = (nodo.incremento != null) ? optimizar(nodo.incremento) : null;
        Nodo cuerpoOpt = optimizar(nodo.cuerpo);
        return new NodoFor(initOpt, condOpt, incOpt, cuerpoOpt);
    }

    @Override
    public Nodo visit(NodoImprimir nodo) {
        Nodo expresionOptimizada = optimizar(nodo.expresion);
        return new NodoImprimir(expresionOptimizada);
    }

    @Override
    public Nodo visit(NodoExpresionBinaria nodo) {
        Nodo izq = optimizar(nodo.izquierda);
        Nodo der = optimizar(nodo.derecha);

        if (izq instanceof NodoNumero && der instanceof NodoNumero) {
            int valIzq = Integer.parseInt(((NodoNumero) izq).valor.getValor());
            int valDer = Integer.parseInt(((NodoNumero) der).valor.getValor());
            switch (nodo.operador.getValor()) {
                case "+":
                    return new NodoNumero(new Token.Builder().conTipo(TipoToken.NUMERO).conValor(String.valueOf(valIzq + valDer)).construir());
                case "-":
                    return new NodoNumero(new Token.Builder().conTipo(TipoToken.NUMERO).conValor(String.valueOf(valIzq - valDer)).construir());
                case "*":
                    return new NodoNumero(new Token.Builder().conTipo(TipoToken.NUMERO).conValor(String.valueOf(valIzq * valDer)).construir());
            }
        }
        return new NodoExpresionBinaria(izq, nodo.operador, der);
    }

    @Override
    public Nodo visit(NodoNumero nodo) {
        return nodo;
    }

    @Override
    public Nodo visit(NodoVariable nodo) {
        return nodo;
    }

    @Override
    public Nodo visit(NodoStringLiteral nodo) {
        return nodo;
    }
}
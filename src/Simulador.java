// Archivo: src/Simulador.java
import java.util.HashMap;
import java.util.Map;

public class Simulador implements Visitor<Object> {

    private Map<String, Object> entorno = new HashMap<>();
    private final StringBuilder consola = new StringBuilder();

    public String simular(Programa programa) {
        try {
            programa.accept(this);
            return consola.toString();
        } catch (Exception e) {
            return "Error en tiempo de ejecución: " + e.getMessage();
        }
    }

    @Override
    public Object visit(Programa programa) {
        for (Nodo sentencia : programa.sentencias) {
            sentencia.accept(this);
        }
        return null;
    }

    // MÉTODO CORREGIDO
    @Override
    public Object visit(NodoBloque nodo) {
        // Un bloque ahora simplemente ejecuta sentencias en el ámbito actual.
        // La gestión del ámbito se delega a las estructuras que lo requieren (for, etc).
        for (Nodo sentencia : nodo.sentencias) {
            sentencia.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(NodoDeclaracion nodo) {
        Object valor = (nodo.expresion != null) ? nodo.expresion.accept(this) : null;
        entorno.put(nodo.identificador.getValor(), valor);
        return null;
    }

    @Override
    public Object visit(NodoAsignacion nodo) {
        String nombre = nodo.identificador.getValor();
        if (!entorno.containsKey(nombre)) {
            throw new RuntimeException("Variable no declarada '" + nombre + "'.");
        }
        Object valor = nodo.expresion.accept(this);
        entorno.put(nombre, valor);
        return valor;
    }

    @Override
    public Object visit(NodoIf nodo) {
        if (esVerdadero(nodo.condicion.accept(this))) {
            nodo.cuerpoIf.accept(this);
        } else if (nodo.cuerpoElse != null) {
            nodo.cuerpoElse.accept(this);
        }
        return null;
    }

    // MÉTODO CORREGIDO
    @Override
    public Object visit(NodoWhile nodo) {
        while (esVerdadero(nodo.condicion.accept(this))) {
            nodo.cuerpo.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(NodoFor nodo) {
        // El 'for' crea su propio ámbito para la variable de inicialización
        Map<String, Object> entornoAnterior = new HashMap<>(this.entorno);

        if (nodo.inicializador != null) {
            nodo.inicializador.accept(this);
        }
        while (nodo.condicion == null || esVerdadero(nodo.condicion.accept(this))) {
            nodo.cuerpo.accept(this);
            if (nodo.incremento != null) {
                nodo.incremento.accept(this);
            }
        }

        this.entorno = entornoAnterior; // Sale del ámbito del 'for'
        return null;
    }

    @Override
    public Object visit(NodoImprimir nodo) {
        Object valor = nodo.expresion.accept(this);
        consola.append(valor.toString()).append("\n");
        return null;
    }

    @Override
    public Object visit(NodoExpresionBinaria nodo) {
        Object izq = nodo.izquierda.accept(this);
        Object der = nodo.derecha.accept(this);

        if (nodo.operador.getValor().equals("+")) {
            if (izq instanceof String || der instanceof String) {
                return izq.toString() + der.toString();
            }
        }

        if (!(izq instanceof Integer) || !(der instanceof Integer)) {
            throw new RuntimeException("Los operandos deben ser números enteros para esta operación.");
        }
        int numIzq = (Integer) izq;
        int numDer = (Integer) der;

        switch (nodo.operador.getValor()) {
            case "+": return numIzq + numDer;
            case "-": return numIzq - numDer;
            case "*": return numIzq * numDer;
            case "/": if (numDer == 0) throw new RuntimeException("División por cero."); return numIzq / numDer;
            case "<": return numIzq < numDer;
            case "<=": return numIzq <= numDer;
            case ">": return numIzq > numDer;
            case ">=": return numIzq >= numDer;
            case "==": return numIzq == numDer;
            case "!=": return numIzq != numDer;
        }
        throw new RuntimeException("Operador desconocido: " + nodo.operador.getValor());
    }

    @Override
    public Object visit(NodoNumero nodo) {
        return Integer.parseInt(nodo.valor.getValor());
    }

    @Override
    public Object visit(NodoVariable nodo) {
        String nombre = nodo.nombre.getValor();
        if (entorno.containsKey(nombre)) {
            return entorno.get(nombre);
        }
        throw new RuntimeException("Variable no definida '" + nombre + "'.");
    }

    @Override
    public Object visit(NodoStringLiteral nodo) {
        String valor = nodo.valor.getValor();
        return valor.substring(1, valor.length() - 1);
    }

    private boolean esVerdadero(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (Boolean) obj;
        if (obj instanceof Integer) return (Integer) obj != 0;
        return true;
    }
}
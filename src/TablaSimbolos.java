import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TablaSimbolos {
    private final Map<String, Simbolo> tabla;
    private final StringBuilder resultados; // Para registrar el proceso

    public TablaSimbolos() {
        this.tabla = new HashMap<>();
        this.resultados = new StringBuilder();
    }

    // Declara un nuevo símbolo (variable) en la tabla
    public boolean declarar(String nombre, TipoToken tipo) {
        if (tabla.containsKey(nombre)) {
            resultados.append(String.format("ERROR SEMÁNTICO: La variable '%s' ya ha sido declarada.\n", nombre));
            return false;
        }
        tabla.put(nombre, new Simbolo(nombre, tipo));
        resultados.append(String.format("OK: Variable '%s' de tipo '%s' declarada.\n", nombre, tipo));
        return true;
    }

    // Busca un símbolo en la tabla para verificar si existe
    public Optional<Simbolo> buscar(String nombre) {
        if (!tabla.containsKey(nombre)) {
            resultados.append(String.format("ERROR SEMÁNTICO: La variable '%s' no ha sido declarada.\n", nombre));
            return Optional.empty();
        }
        return Optional.of(tabla.get(nombre));
    }

    public String getResultados() {
        return resultados.toString();
    }
}
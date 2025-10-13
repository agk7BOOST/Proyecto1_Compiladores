public class Simbolo {
    private final String nombre;
    private final TipoToken tipo; // ej. INT, FLOAT

    public Simbolo(String nombre, TipoToken tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return String.format("<Símbolo nombre='%s', tipo=%s>", nombre, tipo);
    }
}
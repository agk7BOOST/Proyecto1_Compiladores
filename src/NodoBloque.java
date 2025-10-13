// Archivo: src/NodoBloque.java
import java.util.List;

public class NodoBloque implements Nodo {
    public final List<Nodo> sentencias;

    public NodoBloque(List<Nodo> sentencias) {
        this.sentencias = sentencias;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
// Archivo: src/Programa.java
import java.util.List;

public class Programa implements Nodo {
    public final List<Nodo> sentencias;

    public Programa(List<Nodo> sentencias) {
        this.sentencias = sentencias;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
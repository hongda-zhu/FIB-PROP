package scrabble.helpers;

import java.util.Objects;

public class Tuple<X, Y> { 
    public final X x; 
    public final Y y; 

    public Tuple(X x, Y y) { 
        this.x = x; 
        this.y = y; 
    } 

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Compara la misma instancia
        if (!(o instanceof Tuple)) return false; // Verifica si es una instancia de Tuple
        Tuple<?, ?> tuple = (Tuple<?, ?>) o; // Castea el objeto
        return Objects.equals(x, tuple.x) && Objects.equals(y, tuple.y); // Compara los elementos
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y); // Genera un hash code basado en los elementos
    }
}

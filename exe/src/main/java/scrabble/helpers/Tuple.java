package scrabble.helpers;

import java.io.Serializable;
import java.util.Objects;
/**
 * Una clase genérica que representa una tupla inmutable de dos elementos.
 *
 * @param <X> el tipo del primer elemento
 * @param <Y> el tipo del segundo elemento
 */
public class Tuple<X, Y> implements Serializable {
    private static final long serialVersionUID = 1L;
 
    /** El primer valor de la tupla. */
    public final X x; 
    
    /** El segundo valor de la tupla. */
    public final Y y; 

    /**
     * Crea una nueva tupla de dos elementos.
     *
     * @param x el primer valor
     * @param y el segundo valor
     */
    public Tuple(X x, Y y) { 
        this.x = x; 
        this.y = y; 
    } 

    /**
     * Compara este objeto con otro para verificar la igualdad.
     *
     * @param o el objeto a comparar
     * @return {@code true} si ambos elementos son iguales, {@code false} en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Compara la misma instancia
        if (!(o instanceof Tuple)) return false; // Verifica si es una instancia de Tuple
        Tuple<?, ?> tuple = (Tuple<?, ?>) o; // Castea el objeto
        return Objects.equals(x, tuple.x) && Objects.equals(y, tuple.y); // Compara los elementos
    }

    /**
     * Devuelve el valor hash del objeto, calculado a partir de los dos valores.
     *
     * @return el código hash del objeto
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y); // Genera un hash code basado en los elementos
    }
}

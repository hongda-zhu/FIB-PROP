package scrabble.helpers;

import java.io.Serializable;
import java.util.Objects;
/**
 * Una clase genérica que representa una tupla inmutable de tres elementos.
 * 
 * @param <A> el tipo del primer elemento
 * @param <B> el tipo del segundo elemento
 * @param <C> el tipo del tercer elemento
 */
public class Triple<A, B, C> implements Serializable{
    private static final long serialVersionUID = 1L;
    
    /** El primer valor de la tupla. */
    public final A x;
    /** El segundo valor de la tupla. */ 
    public final B y;
    /** El tercer valor de la tupla. */   
    public final C z;

    /**
     * Crea una nueva tupla de tres elementos.
     *
     * @param x el primer valor
     * @param y el segundo valor
     * @param z el tercer valor
     */
    public Triple(A x, B y, C z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Devuelve el primer valor de la tupla.
     *
     * @return el valor de {@code x}
     */
    public A getx() {
        return x;
    }

    /**
     * Devuelve el segundo valor de la tupla.
     *
     * @return el valor de {@code y}
     */
    public B gety() {
        return y;
    }

    /**
     * Devuelve el tercer valor de la tupla.
     *
     * @return el valor de {@code z}
     */
    public C getz() {
        return z;
    }

    /**
     * Compara este objeto con otro para verificar la igualdad.
     *
     * @param o el objeto a comparar
     * @return true si los tres valores son iguales, code false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(x, triple.x) &&
               Objects.equals(y, triple.y) &&
               Objects.equals(z, triple.z);
    }

    /**
     * Devuelve el valor hash del objeto, calculado a partir de los tres valores.
     *
     * @return el código hash del objeto
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
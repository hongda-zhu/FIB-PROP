package scrabble.helpers;

import java.io.Serializable;
import java.util.Objects;
/**
 * Clase genérica que representa una tupla mutable de tres elementos.
 * 
 * Esta clase proporciona una estructura de datos simple para agrupar tres valores
 * relacionados de tipos potencialmente diferentes. Es ampliamente utilizada en el
 * sistema para representar movimientos de Scrabble (palabra, posición, dirección),
 * coordenadas tridimensionales y otros conjuntos de datos relacionados.
 * 
 * Características principales:
 * - Genérica: soporta cualquier tipo de datos para cada elemento
 * - Mutable: permite modificar los valores después de la creación
 * - Serializable: puede ser persistida y transmitida
 * - Implementa equals() y hashCode() para comparaciones correctas
 * - Acceso directo a campos públicos para simplicidad
 * 
 * Casos de uso típicos:
 * - Representación de movimientos: (palabra, posición, dirección)
 * - Coordenadas con metadatos: (x, y, tipo)
 * - Resultados de operaciones: (éxito, valor, mensaje)
 * 
 * @param <A> Tipo del primer elemento (x)
 * @param <B> Tipo del segundo elemento (y)  
 * @param <C> Tipo del tercer elemento (z)
 * 
 * @version 2.0
 * @since 1.0
 */
public class Triple<A, B, C> implements Serializable{
    private static final long serialVersionUID = 1L;
    
    /** El primer valor de la tupla. */
    public A x;
    /** El segundo valor de la tupla. */ 
    public B y;
    /** El tercer valor de la tupla. */   
    public C z;

    /**
     * Constructor que crea una nueva tupla de tres elementos.
     *
     * @param x El primer valor de la tupla.
     * @param y El segundo valor de la tupla.
     * @param z El tercer valor de la tupla.
     * @post Se crea una nueva instancia de Triple con los valores proporcionados.
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
     * Actualiza todos los valores de esta tupla con los valores de otra tupla.
     * Método de conveniencia para copiar completamente el contenido de otra tupla.
     *
     * @param triple La tupla de origen desde donde copiar los valores.
     * @pre El parámetro triple no debe ser null.
     * @post Los valores x, y, z de esta tupla se actualizan con los valores correspondientes de la tupla origen.
     */
    public void setFromTriple(Triple<A, B, C> triple) {
        this.x = triple.getx();
        this.y = triple.gety();
        this.z = triple.getz();
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
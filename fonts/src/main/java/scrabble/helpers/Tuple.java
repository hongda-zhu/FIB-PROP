package scrabble.helpers;

import java.io.Serializable;
import java.util.Objects;
/**
 * Clase genérica que representa una tupla inmutable de dos elementos.
 * 
 * Esta clase proporciona una estructura de datos simple para agrupar dos valores
 * relacionados de tipos potencialmente diferentes. Es fundamental en el sistema
 * para representar coordenadas, pares clave-valor y otros conjuntos de datos
 * relacionados que necesitan ser tratados como una unidad.
 * 
 * Características principales:
 * - Genérica: soporta cualquier tipo de datos para cada elemento
 * - Inmutable: los valores no pueden ser modificados después de la creación
 * - Serializable: puede ser persistida y transmitida
 * - Implementa equals() y hashCode() para comparaciones correctas
 * - Campos públicos finales para acceso directo y eficiente
 * 
 * Casos de uso típicos:
 * - Coordenadas en el tablero: (fila, columna)
 * - Pares de resultados: (éxito, valor)
 * - Rangos: (mínimo, máximo)
 * - Asociaciones: (clave, valor)
 * 
 * @param <X> Tipo del primer elemento (x)
 * @param <Y> Tipo del segundo elemento (y)
 * 
 * @version 2.0
 * @since 1.0
 */
public class Tuple<X, Y> implements Serializable {
    private static final long serialVersionUID = 1L;
 
    /** El primer valor de la tupla. */
    public final X x; 
    
    /** El segundo valor de la tupla. */
    public final Y y; 

    /**
     * Constructor que crea una nueva tupla inmutable de dos elementos.
     *
     * @param x El primer valor de la tupla.
     * @param y El segundo valor de la tupla.
     * @post Se crea una nueva instancia de Tuple con los valores proporcionados,
     *       que permanecerán inmutables durante toda la vida del objeto.
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

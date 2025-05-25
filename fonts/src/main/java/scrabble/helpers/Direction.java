package scrabble.helpers;

/**
 * Enumeración que define las direcciones posibles para colocar palabras en el tablero de Scrabble.
 * 
 * Esta enumeración especifica las dos orientaciones válidas en las que se pueden
 * formar palabras en el juego, determinando cómo se extienden las letras desde
 * una posición inicial en el tablero.
 * 
 * Direcciones disponibles:
 * - HORIZONTAL: Las palabras se forman de izquierda a derecha
 * - VERTICAL: Las palabras se forman de arriba hacia abajo
 * 
 * Se utiliza en la validación de movimientos, búsqueda de jugadas válidas
 * y cálculo de puntuaciones para determinar la orientación de las palabras.
 * 
 * @version 2.0
 * @since 1.0
 */
public enum Direction {
    /** 
     * Dirección horizontal: las palabras se forman de izquierda a derecha.
     * Las coordenadas se incrementan en el eje Y (columnas).
     */
    HORIZONTAL,
    
    /** 
     * Dirección vertical: las palabras se forman de arriba hacia abajo.
     * Las coordenadas se incrementan en el eje X (filas).
     */
    VERTICAL
}
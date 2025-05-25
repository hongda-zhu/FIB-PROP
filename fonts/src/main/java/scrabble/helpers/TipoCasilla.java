package scrabble.helpers;

/**
 * Enumeración que define los tipos de casillas especiales en el tablero de Scrabble.
 * 
 * Esta enumeración especifica los diferentes tipos de casillas que pueden existir
 * en el tablero, cada una con efectos específicos sobre la puntuación de las palabras
 * formadas. Los multiplicadores se aplican según las reglas oficiales del Scrabble.
 * 
 * Tipos de casillas disponibles:
 * - NORMAL: Casilla estándar sin multiplicadores
 * - CENTRO: Casilla central del tablero (inicio obligatorio)
 * - LETRA_DOBLE: Duplica el valor de la letra colocada
 * - LETRA_TRIPLE: Triplica el valor de la letra colocada
 * - PALABRA_DOBLE: Duplica el valor total de la palabra
 * - PALABRA_TRIPLE: Triplica el valor total de la palabra
 * 
 * @version 2.0
 * @since 1.0
 */
public enum TipoCasilla {
    /** 
     * Casilla normal sin efectos especiales.
     * No aplica multiplicadores a letras ni palabras.
     */
    NORMAL,
    
    /** 
     * Casilla central del tablero.
     * Punto de inicio obligatorio para la primera palabra del juego.
     */
    CENTRO,
    
    /** 
     * Casilla que duplica el valor de la letra.
     * Multiplica por 2 el valor de la letra colocada en esta posición.
     */
    LETRA_DOBLE,
    
    /** 
     * Casilla que triplica el valor de la letra.
     * Multiplica por 3 el valor de la letra colocada en esta posición.
     */
    LETRA_TRIPLE,
    
    /** 
     * Casilla que duplica el valor de toda la palabra.
     * Multiplica por 2 el valor total de la palabra que pasa por esta casilla.
     */
    PALABRA_DOBLE,
    
    /** 
     * Casilla que triplica el valor de toda la palabra.
     * Multiplica por 3 el valor total de la palabra que pasa por esta casilla.
     */
    PALABRA_TRIPLE
}
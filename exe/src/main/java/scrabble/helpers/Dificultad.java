package scrabble.helpers;

/**
 * Enumeración que define los niveles de dificultad disponibles para jugadores IA.
 * 
 * Esta enumeración establece los diferentes grados de complejidad que puede
 * tener la inteligencia artificial al jugar Scrabble, afectando directamente
 * a la estrategia de selección de movimientos y la calidad de las jugadas.
 * 
 * Niveles disponibles:
 * - FACIL: La IA selecciona el primer movimiento válido encontrado, sin optimización
 * - DIFICIL: La IA busca y selecciona el movimiento que maximiza la puntuación
 * 
 * @version 2.0
 * @since 1.0
 */
public enum Dificultad {
    /** 
     * Nivel fácil: La IA realiza movimientos básicos sin optimización avanzada.
     * Selecciona el primer movimiento válido encontrado.
     */
    FACIL, 
    
    /** 
     * Nivel difícil: La IA utiliza estrategias avanzadas para maximizar puntuación.
     * Evalúa todos los movimientos posibles y selecciona el óptimo.
     */
    DIFICIL
}
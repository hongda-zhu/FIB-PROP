package scrabble.domain.persistences.interfaces;

import scrabble.domain.models.Ranking;
import scrabble.domain.models.rankingStrategy.PlayerRankingStats;

import java.util.List;
import java.util.Set;

/**
 * Interfaz que define las operaciones de persistencia para el ranking.
 */
public interface RepositorioRanking {
    
    /**
     * Guarda el ranking completo en el sistema de persistencia.
     * 
     * @param ranking Objeto Ranking a guardar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean guardar(Ranking ranking);
    
    /**
     * Carga el ranking desde el sistema de persistencia.
     * 
     * @return El objeto Ranking cargado o un nuevo Ranking si no existe
     */
    Ranking cargar();
    
    /**
     * Actualiza las estadísticas de un jugador específico.
     * 
     * @param nombre Nombre del jugador
     * @param stats Estadísticas actualizadas
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats);
    
    /**
     * Elimina un jugador del ranking.
     * 
     * @param nombre Nombre del jugador a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean eliminarJugador(String nombre);
    
    /**
     * Obtiene una lista de jugadores ordenada según un criterio específico.
     * 
     * @param criterio Criterio de ordenación
     * @return Lista ordenada de nombres de jugadores
     */
    List<String> obtenerRankingOrdenado(String criterio);
    
    /**
     * Obtiene el conjunto de nombres de todos los jugadores en el ranking.
     * 
     * @return Conjunto de nombres de jugadores
     */
    Set<String> obtenerTodosJugadores();
    
    /**
     * Obtiene la puntuación máxima de un jugador específico.
     * 
     * @param nombre Nombre del jugador
     * @return Puntuación máxima del jugador o 0 si no existe
     */
    int obtenerPuntuacionMaxima(String nombre);
    
    /**
     * Obtiene la puntuación media de un jugador específico.
     * 
     * @param nombre Nombre del jugador
     * @return Puntuación media del jugador o 0.0 si no existe
     */
    double obtenerPuntuacionMedia(String nombre);
    
    /**
     * Obtiene el número de partidas jugadas por un jugador específico.
     * 
     * @param nombre Nombre del jugador
     * @return Número de partidas jugadas o 0 si no existe
     */
    int obtenerPartidasJugadas(String nombre);
    
    /**
     * Obtiene el número de victorias de un jugador específico.
     * 
     * @param nombre Nombre del jugador
     * @return Número de victorias o 0 si no existe
     */
    int obtenerVictorias(String nombre);
    
    /**
     * Obtiene la puntuación total acumulada de un jugador específico.
     * 
     * @param nombre Nombre del jugador
     * @return Puntuación total acumulada o 0 si no existe
     */
    int obtenerPuntuacionTotal(String nombre);
    
    /**
     * Verifica si un jugador existe en el ranking.
     * 
     * @param nombre Nombre del jugador
     * @return true si el jugador existe en el ranking, false en caso contrario
     */
    boolean existeJugador(String nombre);
}
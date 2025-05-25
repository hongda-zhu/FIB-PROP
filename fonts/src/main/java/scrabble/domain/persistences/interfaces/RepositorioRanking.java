package scrabble.domain.persistences.interfaces;

import scrabble.domain.models.Ranking;
import scrabble.domain.models.rankingStrategy.PlayerRankingStats;

import java.util.List;
import java.util.Set;

/**
 * Interfaz que define las operaciones de persistencia para el ranking.
 * 
 * Esta interfaz establece el contrato para la gestión de persistencia del sistema
 * de ranking de jugadores, incluyendo operaciones CRUD, consultas estadísticas
 * especializadas y gestión de criterios de ordenación. Implementa el patrón
 * Repository para abstraer los detalles de almacenamiento.
 * 
 * Funcionalidades principales:
 * - Operaciones CRUD completas para el ranking
 * - Consultas estadísticas individuales por jugador
 * - Generación de rankings ordenados por diferentes criterios
 * - Actualización incremental de estadísticas
 * - Gestión de jugadores y sus métricas de rendimiento
 * 
 * @version 2.0
 * @since 1.0
 */
public interface RepositorioRanking {
    
    /**
     * Guarda el ranking completo en el sistema de persistencia.
     * 
     * @param ranking Objeto Ranking con todas las estadísticas de jugadores a persistir.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @pre ranking != null
     * @post Si retorna true, el ranking queda persistido con todas las estadísticas
     */
    boolean guardar(Ranking ranking);
    
    /**
     * Carga el ranking completo desde el sistema de persistencia.
     * 
     * @return El objeto Ranking cargado con todas las estadísticas, o un nuevo Ranking si no existe.
     * @post Retorna un objeto Ranking válido (nuevo si no existe archivo previo)
     */
    Ranking cargar();
    
    /**
     * Actualiza las estadísticas de un jugador específico en el ranking.
     * 
     * @param nombre Nombre único del jugador cuyas estadísticas se actualizarán.
     * @param stats Objeto PlayerRankingStats con las estadísticas actualizadas.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @pre nombre != null && !nombre.isEmpty() && stats != null
     * @post Si retorna true, las estadísticas del jugador quedan actualizadas
     */
    boolean actualizarEstadisticasJugador(String nombre, PlayerRankingStats stats);
    
    /**
     * Elimina un jugador completamente del ranking.
     * 
     * @param nombre Nombre del jugador a eliminar del sistema de ranking.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Si retorna true, el jugador y sus estadísticas han sido eliminados
     */
    boolean eliminarJugador(String nombre);
    
    /**
     * Obtiene una lista de jugadores ordenada según un criterio específico.
     * 
     * @param criterio Criterio de ordenación (puntuación, victorias, partidas, etc.).
     * @return Lista ordenada de nombres de jugadores según el criterio especificado.
     * @pre criterio != null && !criterio.isEmpty()
     * @post Retorna una lista válida ordenada según el criterio (puede estar vacía)
     */
    List<String> obtenerRankingOrdenado(String criterio);
    
    /**
     * Obtiene el conjunto de nombres de todos los jugadores registrados en el ranking.
     * 
     * @return Conjunto de nombres de jugadores presentes en el sistema de ranking.
     * @post Retorna un conjunto válido (puede estar vacío si no hay jugadores)
     */
    Set<String> obtenerTodosJugadores();
    
    /**
     * Obtiene la puntuación máxima alcanzada por un jugador específico.
     * 
     * @param nombre Nombre del jugador para consultar su puntuación máxima.
     * @return Puntuación máxima del jugador o 0 si no existe en el ranking.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna la puntuación máxima sin modificar el estado del ranking
     */
    int obtenerPuntuacionMaxima(String nombre);
    
    /**
     * Obtiene la puntuación media de todas las partidas de un jugador específico.
     * 
     * @param nombre Nombre del jugador para calcular su puntuación media.
     * @return Puntuación media del jugador o 0.0 si no existe en el ranking.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna la media calculada sin modificar el estado del ranking
     */
    double obtenerPuntuacionMedia(String nombre);
    
    /**
     * Obtiene el número total de partidas jugadas por un jugador específico.
     * 
     * @param nombre Nombre del jugador para consultar sus partidas jugadas.
     * @return Número de partidas jugadas o 0 si no existe en el ranking.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna el contador de partidas sin modificar el estado del ranking
     */
    int obtenerPartidasJugadas(String nombre);
    
    /**
     * Obtiene el número total de victorias de un jugador específico.
     * 
     * @param nombre Nombre del jugador para consultar sus victorias.
     * @return Número de victorias o 0 si no existe en el ranking.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna el contador de victorias sin modificar el estado del ranking
     */
    int obtenerVictorias(String nombre);
    
    /**
     * Obtiene la puntuación total acumulada de un jugador específico.
     * 
     * @param nombre Nombre del jugador para consultar su puntuación total.
     * @return Puntuación total acumulada o 0 si no existe en el ranking.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna la puntuación total sin modificar el estado del ranking
     */
    int obtenerPuntuacionTotal(String nombre);
    
    /**
     * Verifica si un jugador existe en el sistema de ranking.
     * 
     * @param nombre Nombre del jugador a verificar en el ranking.
     * @return true si el jugador existe en el ranking, false en caso contrario.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna el estado de existencia sin modificar el ranking
     */
    boolean existeJugador(String nombre);
}
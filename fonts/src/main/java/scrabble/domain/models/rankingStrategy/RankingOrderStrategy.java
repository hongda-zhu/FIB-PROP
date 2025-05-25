package scrabble.domain.models.rankingStrategy;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Interfaz que define la estrategia para ordenar el ranking de jugadores.
 * Actúa como un Comparator especializado para nombres de usuario (String).
 * Las implementaciones de esta interfaz reciben directamente un objeto Ranking
 * para acceder a las estadísticas de los jugadores y aplicar diferentes criterios
 * de ordenación como puntuación máxima, media, total, partidas jugadas o victorias.
 * 
 * Esta interfaz forma parte del patrón Strategy, permitiendo cambiar dinámicamente
 * el algoritmo de ordenación del ranking sin modificar el código cliente.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public interface RankingOrderStrategy extends Serializable, Comparator<String> {
    /**
     * Compara dos nombres de usuario según la estrategia específica.
     * Implementación del método compare de Comparator que define el orden
     * relativo entre dos jugadores según el criterio específico de cada estrategia.
     * Las implementaciones utilizan un objeto Ranking para obtener las estadísticas
     * necesarias para la comparación (puntuaciones, partidas, victorias, etc.).
     * 
     * @param username1 Nombre del primer jugador a comparar
     * @param username2 Nombre del segundo jugador a comparar
     * @return un valor negativo si username1 va antes que username2 en el ranking,
     *         cero si son equivalentes según el criterio de ordenación,
     *         un valor positivo si username1 va después que username2 en el ranking
     * @pre Los nombres de usuario no deben ser null.
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según el criterio de la estrategia.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    @Override
    int compare(String username1, String username2);
    
    /**
     * Devuelve el nombre descriptivo de la estrategia.
     * Proporciona una identificación clara del criterio de ordenación utilizado
     * por esta estrategia, útil para mostrar en interfaces de usuario y logs.
     * 
     * @return Nombre descriptivo de la estrategia (ej: "Puntuación Máxima", "Victorias")
     * @pre No hay precondiciones específicas.
     * @post Se devuelve un string no vacío que describe la estrategia.
     */
    String getNombre();
}
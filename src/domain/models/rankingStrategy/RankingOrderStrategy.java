package domain.models.rankingStrategy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interfaz que define la estrategia para ordenar el ranking de jugadores.
 */
public interface RankingOrderStrategy extends Serializable {
    /**
     * Ordena la lista de usuarios según la estrategia específica.
     * 
     * @param puntuacionesPorUsuario Mapa con las puntuaciones de cada usuario
     * @param puntuacionMaximaPorUsuario Mapa con la puntuación máxima de cada usuario
     * @param puntuacionMediaPorUsuario Mapa con la puntuación media de cada usuario
     * @param partidasJugadasPorUsuario Mapa con el número de partidas jugadas por cada usuario
     * @param victoriasUsuario Mapa con el número de victorias de cada usuario
     * @return Lista ordenada de nombres de usuario según la estrategia
     */
    List<String> ordenarRanking(
        Map<String, List<Integer>> puntuacionesPorUsuario,
        Map<String, Integer> puntuacionMaximaPorUsuario,
        Map<String, Double> puntuacionMediaPorUsuario,
        Map<String, Integer> partidasJugadasPorUsuario,
        Map<String, Integer> victoriasUsuario
    );
    
    /**
     * Devuelve el nombre de la estrategia.
     * 
     * @return Nombre descriptivo de la estrategia
     */
    String getNombre();
}
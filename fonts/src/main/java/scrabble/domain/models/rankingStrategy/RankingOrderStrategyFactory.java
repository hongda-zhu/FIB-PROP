package scrabble.domain.models.rankingStrategy;

import scrabble.domain.models.Ranking;

/**
 * Factory para crear estrategias de ordenación del ranking.
 * Implementa el patrón Factory Method para centralizar la creación de estrategias.
 */
public class RankingOrderStrategyFactory {
    
    /**
     * Crea una estrategia de ordenación del ranking según el criterio especificado.
     * 
     * @pre El ranking no debe ser null.
     * @param criterio Criterio de ordenación:
     *                "maxima" para puntuación máxima,
     *                "media" para puntuación media,
     *                "partidas" para partidas jugadas,
     *                "victorias" para victorias,
     *                "total" para puntuación total acumulada
     * @param ranking Objeto ranking que contiene los datos
     * @return Una estrategia de ordenación según el criterio
     * @post Se devuelve una implementación de RankingOrderStrategy correspondiente al criterio especificado.
     *       Si el criterio es null o no reconocido, se devuelve la estrategia por puntuación total.
     * @throws IllegalArgumentException si ranking es null
     */
    public static RankingOrderStrategy createStrategy(String criterio, Ranking ranking) {
        if (ranking == null) {
            throw new IllegalArgumentException("El ranking no puede ser null");
        }
        
        if (criterio == null) {
            return new PuntuacionTotalStrategy(ranking); // Default cambiado a puntuación total
        }
        
        switch (criterio.toLowerCase()) {
            case "maxima":
                return new MaximaScoreStrategy(ranking);
            case "media":
                return new MediaScoreStrategy(ranking);
            case "partidas":
                return new PartidasJugadasStrategy(ranking);
            case "victorias":
                return new VictoriasStrategy(ranking);
            case "total":
                return new PuntuacionTotalStrategy(ranking);
            default:
                return new PuntuacionTotalStrategy(ranking); // Default cambiado a puntuación total
        }
    }
}
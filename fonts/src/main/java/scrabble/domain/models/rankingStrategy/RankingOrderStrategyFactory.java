package scrabble.domain.models.rankingStrategy;

/**
 * Factory para crear estrategias de ordenación de ranking.
 */
public class RankingOrderStrategyFactory {
    
    /**
     * Crea una estrategia de ordenación según el criterio especificado.
     * 
     * @param criterio Criterio de ordenación: "maxima", "media", "partidas" o "victorias"
     * @return Estrategia correspondiente o MaximaScoreStrategy por defecto
     */
    public static RankingOrderStrategy createStrategy(String criterio) {
        if (criterio == null) {
            return new MaximaScoreStrategy(); // Default
        }
        
        switch (criterio.toLowerCase()) {
            case "maxima":
                return new MaximaScoreStrategy();
            case "media":
                return new MediaScoreStrategy();
            case "partidas":
                return new PartidasJugadasStrategy();
            case "victorias":
                return new RatioVictoriasStrategy();
            default:
                return new MaximaScoreStrategy(); // Default
        }
    }
}
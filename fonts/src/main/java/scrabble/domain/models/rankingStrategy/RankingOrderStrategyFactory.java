package scrabble.domain.models.rankingStrategy;

/**
 * Factory para crear estrategias de ordenación de ranking.
 */
public class RankingOrderStrategyFactory {
    
    /**
     * Crea una estrategia de ordenación según el criterio especificado.
     * 
     * @param criterio Criterio de ordenación: "maxima", "media", "partidas" o "victorias"
     * @param dataProvider Proveedor de datos para el acceso a las estadísticas
     * @return Estrategia correspondiente o MaximaScoreStrategy por defecto
     */
    public static RankingOrderStrategy createStrategy(String criterio, RankingDataProvider dataProvider) {
        if (dataProvider == null) {
            throw new IllegalArgumentException("El proveedor de datos no puede ser null");
        }
        
        if (criterio == null) {
            return new MaximaScoreStrategy(dataProvider); // Default
        }
        
        switch (criterio.toLowerCase()) {
            case "maxima":
                return new MaximaScoreStrategy(dataProvider);
            case "media":
                return new MediaScoreStrategy(dataProvider);
            case "partidas":
                return new PartidasJugadasStrategy(dataProvider);
            case "victorias":
            case "ratio": // Mantener compatibilidad con código existente
                return new VictoriasStrategy(dataProvider);
            default:
                return new MaximaScoreStrategy(dataProvider); // Default
        }
    }
}
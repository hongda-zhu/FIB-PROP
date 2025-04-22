package scrabble.domain.models.rankingStrategy;

/**
 * Factory para crear estrategias de ordenación del ranking.
 * Implementa el patrón Factory Method para centralizar la creación de estrategias.
 */
public class RankingOrderStrategyFactory {
    
    /**
     * Crea una estrategia de ordenación del ranking según el criterio especificado.
     * 
     * @pre El dataProvider no debe ser null.
     * @param criterio Criterio de ordenación:
     *                "maxima" para puntuación máxima,
     *                "media" para puntuación media,
     *                "partidas" para partidas jugadas,
     *                "victorias" para victorias,
     *                "total" para puntuación total acumulada
     * @param dataProvider Proveedor de datos para la estrategia
     * @return Una estrategia de ordenación según el criterio
     * @post Se devuelve una implementación de RankingOrderStrategy correspondiente al criterio especificado.
     *       Si el criterio es null o no reconocido, se devuelve la estrategia por puntuación total.
     * @throws IllegalArgumentException si dataProvider es null
     */
    public static RankingOrderStrategy createStrategy(String criterio, RankingDataProvider dataProvider) {
        if (dataProvider == null) {
            throw new IllegalArgumentException("El proveedor de datos no puede ser null");
        }
        
        if (criterio == null) {
            return new PuntuacionTotalStrategy(dataProvider); // Default cambiado a puntuación total
        }
        
        switch (criterio.toLowerCase()) {
            case "maxima":
                return new MaximaScoreStrategy(dataProvider);
            case "media":
                return new MediaScoreStrategy(dataProvider);
            case "partidas":
                return new PartidasJugadasStrategy(dataProvider);
            case "victorias":
                return new VictoriasStrategy(dataProvider);
            case "total":
                return new PuntuacionTotalStrategy(dataProvider);
            default:
                return new PuntuacionTotalStrategy(dataProvider); // Default cambiado a puntuación total
        }
    }
}
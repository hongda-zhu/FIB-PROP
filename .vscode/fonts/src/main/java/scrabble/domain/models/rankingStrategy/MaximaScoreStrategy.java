package scrabble.domain.models.rankingStrategy;

/**
 * Implementación de RankingOrderStrategy que ordena por puntuación máxima.
 */
public class MaximaScoreStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final RankingDataProvider dataProvider;
    
    /**
     * Constructor que recibe el proveedor de datos para acceder a las estadísticas.
     * 
     * @param dataProvider Proveedor de datos de ranking
     */
    public MaximaScoreStrategy(RankingDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    @Override
    public int compare(String username1, String username2) {
        // Obtener las puntuaciones máximas de cada usuario
        int score1 = dataProvider.getPuntuacionMaxima(username1);
        int score2 = dataProvider.getPuntuacionMaxima(username2);
        
        // Comparar por puntuación máxima (orden descendente)
        int comparacion = Integer.compare(score2, score1);
        
        // Si las puntuaciones son iguales, ordenar alfabéticamente
        if (comparacion == 0) {
            return username1.compareTo(username2);
        }
        
        return comparacion;
    }
    
    @Override
    public String getNombre() {
        return "Puntuación Máxima";
    }
}
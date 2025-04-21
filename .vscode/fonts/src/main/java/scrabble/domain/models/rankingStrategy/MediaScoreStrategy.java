package scrabble.domain.models.rankingStrategy;

/**
 * Implementación de RankingOrderStrategy que ordena por puntuación media.
 */
public class MediaScoreStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final RankingDataProvider dataProvider;
    
    /**
     * Constructor que recibe el proveedor de datos para acceder a las estadísticas.
     * 
     * @param dataProvider Proveedor de datos de ranking
     */
    public MediaScoreStrategy(RankingDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    @Override
    public int compare(String username1, String username2) {
        // Obtener las puntuaciones medias de cada usuario
        double score1 = dataProvider.getPuntuacionMedia(username1);
        double score2 = dataProvider.getPuntuacionMedia(username2);
        
        // Comparar por puntuación media (orden descendente)
        int comparacion = Double.compare(score2, score1);
        
        // Si las puntuaciones son iguales, ordenar alfabéticamente
        if (comparacion == 0) {
            return username1.compareTo(username2);
        }
        
        return comparacion;
    }
    
    @Override
    public String getNombre() {
        return "Puntuación Media";
    }
}
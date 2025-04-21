package scrabble.domain.models.rankingStrategy;

/**
 * Implementación de RankingOrderStrategy que ordena por número de partidas jugadas.
 */
public class PartidasJugadasStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final RankingDataProvider dataProvider;
    
    /**
     * Constructor que recibe el proveedor de datos para acceder a las estadísticas.
     * 
     * @param dataProvider Proveedor de datos de ranking
     */
    public PartidasJugadasStrategy(RankingDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    @Override
    public int compare(String username1, String username2) {
        // Obtener el número de partidas jugadas de cada usuario
        int partidas1 = dataProvider.getPartidasJugadas(username1);
        int partidas2 = dataProvider.getPartidasJugadas(username2);
        
        // Comparar por número de partidas (orden descendente)
        int comparacion = Integer.compare(partidas2, partidas1);
        
        // Si el número de partidas es igual, ordenar alfabéticamente
        if (comparacion == 0) {
            return username1.compareTo(username2);
        }
        
        return comparacion;
    }
    
    @Override
    public String getNombre() {
        return "Partidas Jugadas";
    }
}
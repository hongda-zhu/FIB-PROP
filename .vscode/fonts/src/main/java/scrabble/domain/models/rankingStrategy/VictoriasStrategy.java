package scrabble.domain.models.rankingStrategy;

/**
 * Implementación de RankingOrderStrategy que ordena por total de victorias.
 */
public class VictoriasStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final RankingDataProvider dataProvider;
    
    /**
     * Constructor que recibe el proveedor de datos para acceder a las estadísticas.
     * 
     * @param dataProvider Proveedor de datos de ranking
     */
    public VictoriasStrategy(RankingDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    @Override
    public int compare(String username1, String username2) {
        // Obtener el número de victorias de cada usuario
        int victorias1 = dataProvider.getVictorias(username1);
        int victorias2 = dataProvider.getVictorias(username2);
        
        // Comparar por número de victorias (orden descendente)
        int comparacion = Integer.compare(victorias2, victorias1);
        
        // Si el número de victorias es igual, ordenar alfabéticamente
        if (comparacion == 0) {
            return username1.compareTo(username2);
        }
        
        return comparacion;
    }
    
    @Override
    public String getNombre() {
        return "Victorias";
    }
}
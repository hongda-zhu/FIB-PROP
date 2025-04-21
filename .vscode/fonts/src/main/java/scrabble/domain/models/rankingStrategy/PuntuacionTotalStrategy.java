package scrabble.domain.models.rankingStrategy;

/**
 * Estrategia para ordenar el ranking por puntuación total acumulada.
 * Implementa RankingOrderStrategy para integrarse en el sistema de ranking.
 */
public class PuntuacionTotalStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private RankingDataProvider dataProvider;
    
    /**
     * Constructor que recibe el proveedor de datos del ranking.
     * 
     * @param dataProvider Proveedor de datos para acceder a las estadísticas
     */
    public PuntuacionTotalStrategy(RankingDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    /**
     * Compara dos jugadores para ordenarlos por puntuación total (de mayor a menor).
     * 
     * @param jugador1 Nombre del primer jugador
     * @param jugador2 Nombre del segundo jugador
     * @return Valor negativo si jugador1 tiene mayor puntuación total, valor positivo si jugador2 tiene mayor puntuación total,
     *         0 si ambos tienen la misma puntuación total
     */
    @Override
    public int compare(String jugador1, String jugador2) {
        // Ordenamos de mayor a menor puntuación total
        int puntuacionTotal1 = dataProvider.getPuntuacionTotal(jugador1);
        int puntuacionTotal2 = dataProvider.getPuntuacionTotal(jugador2);
        
        // Comparación inversa: mayor puntuación primero
        return Integer.compare(puntuacionTotal2, puntuacionTotal1);
    }
    
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * 
     * @return Nombre de la estrategia
     */
    @Override
    public String getNombre() {
        return "Ranking por Puntuación Total Acumulada";
    }
} 
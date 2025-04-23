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
     * @pre El dataProvider no debe ser null.
     * @param dataProvider Proveedor de datos para acceder a las estadísticas
     * @post Se inicializa la estrategia con el proveedor de datos especificado.
     * @throws NullPointerException si dataProvider es null
     */
    public PuntuacionTotalStrategy(RankingDataProvider dataProvider) {
        if (dataProvider == null) {
            throw new NullPointerException("El proveedor de datos no puede ser null");
        }
        this.dataProvider = dataProvider;
    }
    
    /**
     * Compara dos jugadores para ordenarlos por puntuación total (de mayor a menor).
     * 
     * @pre Los nombres de jugador no deben ser null.
     * @param jugador1 Nombre del primer jugador
     * @param jugador2 Nombre del segundo jugador
     * @return Valor negativo si jugador1 tiene mayor puntuación total, 
     *         valor positivo si jugador2 tiene mayor puntuación total,
     *         0 si ambos tienen la misma puntuación total
     * @post Se devuelve un valor que indica el orden relativo de los jugadores según su puntuación total.
     * @throws NullPointerException si alguno de los nombres de jugador es null
     */
    @Override
    public int compare(String jugador1, String jugador2) {
        if (jugador1 == null || jugador2 == null) {
            throw new NullPointerException("Los nombres de jugador no pueden ser null");
        }
        
        // Ordenamos de mayor a menor puntuación total
        int puntuacionTotal1 = dataProvider.getPuntuacionTotal(jugador1);
        int puntuacionTotal2 = dataProvider.getPuntuacionTotal(jugador2);
        
        // Comparación inversa: mayor puntuación primero
        return Integer.compare(puntuacionTotal2, puntuacionTotal1);
    }
    
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de la estrategia
     * @post Se devuelve el string "Puntuación Total" como nombre de la estrategia.
     */
    @Override
    public String getNombre() {
        return "Puntuación Total";
    }
} 
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
     * @pre El dataProvider no debe ser null.
     * @param dataProvider Proveedor de datos de ranking
     * @post Se inicializa la estrategia con el proveedor de datos especificado.
     * @throws NullPointerException si dataProvider es null
     */
    public VictoriasStrategy(RankingDataProvider dataProvider) {
        if (dataProvider == null) {
            throw new NullPointerException("El proveedor de datos no puede ser null");
        }
        this.dataProvider = dataProvider;
    }
    
    @Override
    /**
     * Compara dos usuarios según su número de victorias (orden descendente).
     * 
     * @pre Los nombres de usuario no deben ser null.
     * @param username1 Nombre del primer usuario
     * @param username2 Nombre del segundo usuario
     * @return valor negativo si username1 va después de username2,
     *         valor positivo si username1 va antes que username2,
     *         cero si tienen el mismo número de victorias (se ordena alfabéticamente)
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según su número de victorias.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    public int compare(String username1, String username2) {
        if (username1 == null || username2 == null) {
            throw new NullPointerException("Los nombres de usuario no pueden ser null");
        }
        
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
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de la estrategia
     * @post Se devuelve el string "Victorias" como nombre de la estrategia.
     */
    public String getNombre() {
        return "Victorias";
    }
}
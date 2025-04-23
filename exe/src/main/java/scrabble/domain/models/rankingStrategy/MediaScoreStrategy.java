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
     * @pre El dataProvider no debe ser null.
     * @param dataProvider Proveedor de datos de ranking
     * @post Se inicializa la estrategia con el proveedor de datos especificado.
     * @throws NullPointerException si dataProvider es null
     */
    public MediaScoreStrategy(RankingDataProvider dataProvider) {
        if (dataProvider == null) {
            throw new NullPointerException("El proveedor de datos no puede ser null");
        }
        this.dataProvider = dataProvider;
    }
    
    @Override
    /**
     * Compara dos usuarios según su puntuación media (orden descendente).
     * 
     * @pre Los nombres de usuario no deben ser null.
     * @param username1 Nombre del primer usuario
     * @param username2 Nombre del segundo usuario
     * @return valor negativo si username1 va después de username2,
     *         valor positivo si username1 va antes que username2,
     *         cero si tienen la misma puntuación media (se ordena alfabéticamente)
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según su puntuación media.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    public int compare(String username1, String username2) {
        if (username1 == null || username2 == null) {
            throw new NullPointerException("Los nombres de usuario no pueden ser null");
        }
        
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
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de la estrategia
     * @post Se devuelve el string "Puntuación Media" como nombre de la estrategia.
     */
    public String getNombre() {
        return "Puntuación Media";
    }
}
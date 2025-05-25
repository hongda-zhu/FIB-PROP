package scrabble.domain.models.rankingStrategy;

import scrabble.domain.models.Ranking;

/**
 * Implementación de RankingOrderStrategy que ordena por número de partidas jugadas.
 */
public class PartidasJugadasStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final Ranking ranking;
    
    /**
     * Constructor que recibe el objeto ranking para acceder a las estadísticas.
     * 
     * @pre El ranking no debe ser null.
     * @param ranking Objeto ranking
     * @post Se inicializa la estrategia con el ranking especificado.
     * @throws NullPointerException si ranking es null
     */
    public PartidasJugadasStrategy(Ranking ranking) {
        if (ranking == null) {
            throw new NullPointerException("El ranking no puede ser null");
        }
        this.ranking = ranking;
    }
    
    @Override
    /**
     * Compara dos usuarios según su número de partidas jugadas (orden descendente).
     * 
     * @pre Los nombres de usuario no deben ser null.
     * @param username1 Nombre del primer usuario
     * @param username2 Nombre del segundo usuario
     * @return valor negativo si username1 va después de username2,
     *         valor positivo si username1 va antes que username2,
     *         cero si tienen el mismo número de partidas (se ordena alfabéticamente)
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según sus partidas jugadas.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    public int compare(String username1, String username2) {
        if (username1 == null || username2 == null) {
            throw new NullPointerException("Los nombres de usuario no pueden ser null");
        }
        
        // Obtener el número de partidas jugadas de cada usuario
        int partidas1 = ranking.getPartidasJugadas(username1);
        int partidas2 = ranking.getPartidasJugadas(username2);
        
        // Comparar por número de partidas (orden descendente)
        int comparacion = Integer.compare(partidas2, partidas1);
        
        // Si el número de partidas es igual, ordenar alfabéticamente
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
     * @post Se devuelve el string "Partidas Jugadas" como nombre de la estrategia.
     */
    public String getNombre() {
        return "Partidas Jugadas";
    }
}
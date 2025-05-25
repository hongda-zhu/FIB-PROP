package scrabble.domain.models.rankingStrategy;

import scrabble.domain.models.Ranking;

/**
 * Implementación de RankingOrderStrategy que ordena por número de partidas jugadas.
 * Esta estrategia ordena a los jugadores según el número total de partidas que han
 * jugado, de mayor a menor. Es útil para identificar a los jugadores más activos
 * del sistema. En caso de empate, se ordena alfabéticamente por nombre de usuario.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public class PartidasJugadasStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final Ranking ranking;
    
    /**
     * Constructor que recibe el objeto ranking para acceder a las estadísticas.
     * Inicializa la estrategia con una referencia al ranking que contiene todos
     * los datos necesarios para acceder al número de partidas jugadas por cada jugador.
     * 
     * @param ranking Objeto ranking que contiene las estadísticas de todos los jugadores
     * @pre El ranking no debe ser null.
     * @post Se inicializa la estrategia con el ranking especificado.
     * @throws NullPointerException si ranking es null
     */
    public PartidasJugadasStrategy(Ranking ranking) {
        if (ranking == null) {
            throw new NullPointerException("El ranking no puede ser null");
        }
        this.ranking = ranking;
    }
    
    /**
     * Compara dos usuarios según su número de partidas jugadas (orden descendente).
     * Utiliza el contador total de partidas jugadas por cada usuario para determinar
     * el orden en el ranking. Los jugadores con más partidas aparecen primero,
     * lo que permite identificar a los usuarios más activos. En caso de empate,
     * se ordena alfabéticamente por nombre de usuario.
     * 
     * @param username1 Nombre del primer usuario a comparar
     * @param username2 Nombre del segundo usuario a comparar
     * @return valor negativo si username1 va después de username2 en el ranking,
     *         valor positivo si username1 va antes que username2 en el ranking,
     *         cero si tienen el mismo número de partidas (se ordena alfabéticamente)
     * @pre Los nombres de usuario no deben ser null.
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según sus partidas jugadas.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    @Override
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
    
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * Proporciona una identificación clara del criterio de ordenación utilizado
     * por esta estrategia para mostrar en interfaces de usuario.
     * 
     * @return Nombre descriptivo de la estrategia: "Partidas Jugadas"
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el string "Partidas Jugadas" como nombre de la estrategia.
     */
    @Override
    public String getNombre() {
        return "Partidas Jugadas";
    }
}
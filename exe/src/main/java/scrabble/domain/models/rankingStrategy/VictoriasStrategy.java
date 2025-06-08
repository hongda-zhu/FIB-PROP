package scrabble.domain.models.rankingStrategy;

import scrabble.domain.models.Ranking;

/**
 * Implementación de RankingOrderStrategy que ordena por total de victorias.
 * Esta estrategia ordena a los jugadores según el número total de partidas
 * que han ganado, de mayor a menor. Es útil para identificar a los jugadores
 * más exitosos del sistema. En caso de empate, se ordena alfabéticamente
 * por nombre de usuario.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public class VictoriasStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final Ranking ranking;
    
    /**
     * Constructor que recibe el objeto ranking para acceder a las estadísticas.
     * Inicializa la estrategia con una referencia al ranking que contiene todos
     * los datos necesarios para acceder al número de victorias de cada jugador.
     * 
     * @param ranking Objeto ranking que contiene las estadísticas de todos los jugadores
     * @pre El ranking no debe ser null.
     * @post Se inicializa la estrategia con el ranking especificado.
     * @throws NullPointerException si ranking es null
     */
    public VictoriasStrategy(Ranking ranking) {
        if (ranking == null) {
            throw new NullPointerException("El ranking no puede ser null");
        }
        this.ranking = ranking;
    }
    
    /**
     * Compara dos usuarios según su número de victorias (orden descendente).
     * Utiliza el contador total de victorias de cada usuario para determinar
     * el orden en el ranking. Los jugadores con más victorias aparecen primero,
     * lo que permite identificar a los usuarios más exitosos. En caso de empate,
     * se ordena alfabéticamente por nombre de usuario.
     * 
     * @param username1 Nombre del primer usuario a comparar
     * @param username2 Nombre del segundo usuario a comparar
     * @return valor negativo si username1 va después de username2 en el ranking,
     *         valor positivo si username1 va antes que username2 en el ranking,
     *         cero si tienen el mismo número de victorias (se ordena alfabéticamente)
     * @pre Los nombres de usuario no deben ser null.
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según su número de victorias.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    @Override
    public int compare(String username1, String username2) {
        if (username1 == null || username2 == null) {
            throw new NullPointerException("Los nombres de usuario no pueden ser null");
        }
        
        // Obtener el número de victorias de cada usuario
        int victorias1 = ranking.getVictorias(username1);
        int victorias2 = ranking.getVictorias(username2);
        
        // Comparar por número de victorias (orden descendente)
        int comparacion = Integer.compare(victorias2, victorias1);
        
        // Si el número de victorias es igual, ordenar alfabéticamente
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
     * @return Nombre descriptivo de la estrategia: "Victorias"
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el string "Victorias" como nombre de la estrategia.
     */
    @Override
    public String getNombre() {
        return "Victorias";
    }
}
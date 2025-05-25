package scrabble.domain.models.rankingStrategy;

import scrabble.domain.models.Ranking;

/**
 * Implementación de RankingOrderStrategy que ordena por puntuación máxima.
 * Esta estrategia ordena a los jugadores según la puntuación más alta que han conseguido
 * en una sola partida, de mayor a menor. En caso de empate, se ordena alfabéticamente.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public class MaximaScoreStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final Ranking ranking;
    
    /**
     * Constructor que recibe el objeto ranking para acceder a las estadísticas.
     * Inicializa la estrategia con una referencia al ranking que contiene todos
     * los datos necesarios para realizar las comparaciones.
     * 
     * @param ranking Objeto ranking que contiene las estadísticas de todos los jugadores
     * @pre El ranking no debe ser null.
     * @post Se inicializa la estrategia con el ranking especificado.
     * @throws NullPointerException si ranking es null
     */
    public MaximaScoreStrategy(Ranking ranking) {
        if (ranking == null) {
            throw new NullPointerException("El ranking no puede ser null");
        }
        this.ranking = ranking;
    }
    
    /**
     * Compara dos usuarios según su puntuación máxima (orden descendente).
     * Utiliza la puntuación más alta conseguida por cada jugador en cualquier partida
     * para determinar el orden. Si ambos jugadores tienen la misma puntuación máxima,
     * se ordena alfabéticamente por nombre de usuario.
     * 
     * @param username1 Nombre del primer usuario a comparar
     * @param username2 Nombre del segundo usuario a comparar
     * @return valor negativo si username1 va después de username2 en el ranking,
     *         valor positivo si username1 va antes que username2 en el ranking,
     *         cero si tienen la misma puntuación máxima (se ordena alfabéticamente)
     * @pre Los nombres de usuario no deben ser null.
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según su puntuación máxima.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    @Override
    public int compare(String username1, String username2) {
        if (username1 == null || username2 == null) {
            throw new NullPointerException("Los nombres de usuario no pueden ser null");
        }
        
        // Obtener las puntuaciones máximas de cada usuario
        int score1 = ranking.getPuntuacionMaxima(username1);
        int score2 = ranking.getPuntuacionMaxima(username2);
        
        // Comparar por puntuación máxima (orden descendente)
        int comparacion = Integer.compare(score2, score1);
        
        // Si las puntuaciones son iguales, ordenar alfabéticamente
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
     * @return Nombre descriptivo de la estrategia: "Puntuación Máxima"
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el string "Puntuación Máxima" como nombre de la estrategia.
     */
    @Override
    public String getNombre() {
        return "Puntuación Máxima";
    }
}
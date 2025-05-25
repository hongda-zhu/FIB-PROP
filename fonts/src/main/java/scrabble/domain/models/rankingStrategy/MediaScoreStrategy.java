package scrabble.domain.models.rankingStrategy;

import scrabble.domain.models.Ranking;

/**
 * Implementación de RankingOrderStrategy que ordena por puntuación media.
 * Esta estrategia calcula la puntuación promedio de todas las partidas jugadas
 * por cada jugador y los ordena de mayor a menor puntuación media. En caso de empate,
 * se ordena alfabéticamente por nombre de usuario.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public class MediaScoreStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private final Ranking ranking;
    
    /**
     * Constructor que recibe el objeto ranking para acceder a las estadísticas.
     * Inicializa la estrategia con una referencia al ranking que contiene todos
     * los datos necesarios para calcular las puntuaciones medias y realizar comparaciones.
     * 
     * @param ranking Objeto ranking que contiene las estadísticas de todos los jugadores
     * @pre El ranking no debe ser null.
     * @post Se inicializa la estrategia con el ranking especificado.
     * @throws NullPointerException si ranking es null
     */
    public MediaScoreStrategy(Ranking ranking) {
        if (ranking == null) {
            throw new NullPointerException("El ranking no puede ser null");
        }
        this.ranking = ranking;
    }
    
    /**
     * Compara dos usuarios según su puntuación media (orden descendente).
     * Calcula la puntuación promedio de todas las partidas jugadas por cada usuario
     * y los compara. La puntuación media se calcula dividiendo la suma total de
     * puntuaciones entre el número de partidas jugadas. En caso de empate,
     * se ordena alfabéticamente por nombre de usuario.
     * 
     * @param username1 Nombre del primer usuario a comparar
     * @param username2 Nombre del segundo usuario a comparar
     * @return valor negativo si username1 va después de username2 en el ranking,
     *         valor positivo si username1 va antes que username2 en el ranking,
     *         cero si tienen la misma puntuación media (se ordena alfabéticamente)
     * @pre Los nombres de usuario no deben ser null.
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según su puntuación media.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    @Override
    public int compare(String username1, String username2) {
        if (username1 == null || username2 == null) {
            throw new NullPointerException("Los nombres de usuario no pueden ser null");
        }
        
        // Obtener las puntuaciones medias de cada usuario
        double score1 = ranking.getPuntuacionMedia(username1);
        double score2 = ranking.getPuntuacionMedia(username2);
        
        // Comparar por puntuación media (orden descendente)
        int comparacion = Double.compare(score2, score1);
        
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
     * @return Nombre descriptivo de la estrategia: "Puntuación Media"
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el string "Puntuación Media" como nombre de la estrategia.
     */
    @Override
    public String getNombre() {
        return "Puntuación Media";
    }
}
package scrabble.domain.models.rankingStrategy;

import scrabble.domain.models.Ranking;

/**
 * Estrategia para ordenar el ranking por puntuación total acumulada.
 * Esta estrategia suma todas las puntuaciones obtenidas por cada jugador
 * a lo largo de todas sus partidas y los ordena de mayor a menor puntuación total.
 * Es útil para identificar a los jugadores con mejor rendimiento acumulativo.
 * En caso de empate, se ordena alfabéticamente por nombre de usuario.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public class PuntuacionTotalStrategy implements RankingOrderStrategy {
    private static final long serialVersionUID = 1L;
    private Ranking ranking;
    
    /**
     * Constructor que recibe el objeto ranking.
     * Inicializa la estrategia con una referencia al ranking que contiene todos
     * los datos necesarios para calcular las puntuaciones totales acumuladas.
     * 
     * @param ranking Objeto ranking para acceder a las estadísticas de todos los jugadores
     * @pre El ranking no debe ser null.
     * @post Se inicializa la estrategia con el ranking especificado.
     * @throws NullPointerException si ranking es null
     */
    public PuntuacionTotalStrategy(Ranking ranking) {
        if (ranking == null) {
            throw new NullPointerException("El ranking no puede ser null");
        }
        this.ranking = ranking;
    }
    
    /**
     * Compara dos jugadores para ordenarlos por puntuación total (de mayor a menor).
     * Utiliza la suma total de todas las puntuaciones obtenidas por cada jugador
     * en todas sus partidas para determinar el orden. Los jugadores con mayor
     * puntuación total acumulada aparecen primero en el ranking. En caso de empate,
     * se ordena alfabéticamente por nombre de usuario.
     * 
     * @param jugador1 Nombre del primer jugador a comparar
     * @param jugador2 Nombre del segundo jugador a comparar
     * @return Valor negativo si jugador1 tiene mayor puntuación total, 
     *         valor positivo si jugador2 tiene mayor puntuación total,
     *         0 si ambos tienen la misma puntuación total (se ordena alfabéticamente)
     * @pre Los nombres de jugador no deben ser null.
     * @post Se devuelve un valor que indica el orden relativo de los jugadores según su puntuación total.
     * @throws NullPointerException si alguno de los nombres de jugador es null
     */
    @Override
    public int compare(String jugador1, String jugador2) {
        if (jugador1 == null || jugador2 == null) {
            throw new NullPointerException("Los nombres de jugador no pueden ser null");
        }
        
        // Ordenamos de mayor a menor puntuación total
        int puntuacionTotal1 = ranking.getPuntuacionTotal(jugador1);
        int puntuacionTotal2 = ranking.getPuntuacionTotal(jugador2);
        
        // Comparación inversa: mayor puntuación primero
        return Integer.compare(puntuacionTotal2, puntuacionTotal1);
    }
    
    /**
     * Obtiene el nombre descriptivo de la estrategia.
     * Proporciona una identificación clara del criterio de ordenación utilizado
     * por esta estrategia para mostrar en interfaces de usuario.
     * 
     * @return Nombre descriptivo de la estrategia: "Puntuación Total"
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el string "Puntuación Total" como nombre de la estrategia.
     */
    @Override
    public String getNombre() {
        return "Puntuación Total";
    }
} 
package scrabble.domain.models.rankingStrategy;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Interfaz que define la estrategia para ordenar el ranking de jugadores.
 * Actúa como un Comparator especializado para nombres de usuario (String).
 */
public interface RankingOrderStrategy extends Serializable, Comparator<String> {
    /**
     * Compara dos nombres de usuario según la estrategia específica.
     * Implementación del método compare de Comparator.
     * 
     * @pre Los nombres de usuario no deben ser null.
     * @param username1 Nombre del primer jugador
     * @param username2 Nombre del segundo jugador
     * @return un valor negativo si username1 va antes que username2, cero si son iguales,
     *         un valor positivo si username1 va después que username2
     * @post Se devuelve un valor que indica el orden relativo de los usuarios según el criterio de la estrategia.
     * @throws NullPointerException si alguno de los nombres de usuario es null
     */
    @Override
    int compare(String username1, String username2);
    
    /**
     * Devuelve el nombre de la estrategia.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre descriptivo de la estrategia
     * @post Se devuelve un string no vacío que describe la estrategia.
     */
    String getNombre();
}
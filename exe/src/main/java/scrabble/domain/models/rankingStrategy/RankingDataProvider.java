package scrabble.domain.models.rankingStrategy;

import java.io.Serializable;

/**
 * Interfaz para proveedores de datos de ranking.
 * Abstrae el acceso a la información de puntuaciones sin acoplar al origen de datos específico.
 */
public interface RankingDataProvider extends Serializable {
    /**
     * Obtiene la puntuación máxima de un usuario.
     * 
     * @pre El username no debe ser null.
     * @param username Nombre del usuario
     * @return Puntuación máxima o 0 si no existe
     * @post Se devuelve un entero no negativo que representa la puntuación máxima del usuario.
     * @throws NullPointerException si username es null
     */
    int getPuntuacionMaxima(String username);
    
    /**
     * Obtiene la puntuación media de un usuario.
     * 
     * @pre El username no debe ser null.
     * @param username Nombre del usuario
     * @return Puntuación media o 0.0 si no existe
     * @post Se devuelve un número decimal no negativo que representa la puntuación media del usuario.
     * @throws NullPointerException si username es null
     */
    double getPuntuacionMedia(String username);
    
    /**
     * Obtiene el número de partidas jugadas por un usuario.
     * 
     * @pre El username no debe ser null.
     * @param username Nombre del usuario
     * @return Número de partidas o 0 si no existe
     * @post Se devuelve un entero no negativo que representa el número de partidas jugadas por el usuario.
     * @throws NullPointerException si username es null
     */
    int getPartidasJugadas(String username);
    
    /**
     * Obtiene el número de victorias de un usuario.
     * 
     * @pre El username no debe ser null.
     * @param username Nombre del usuario
     * @return Número de victorias o 0 si no existe
     * @post Se devuelve un entero no negativo que representa el número de victorias del usuario.
     * @throws NullPointerException si username es null
     */
    int getVictorias(String username);
    
    /**
     * Obtiene la puntuación total acumulada de un usuario.
     * 
     * @pre El username no debe ser null.
     * @param username Nombre del usuario
     * @return Puntuación total acumulada o 0 si no existe
     * @post Se devuelve un entero no negativo que representa la puntuación total acumulada del usuario.
     * @throws NullPointerException si username es null
     */
    int getPuntuacionTotal(String username);
} 
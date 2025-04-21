package scrabble.domain.models.rankingStrategy;

import java.io.Serializable;

/**
 * Interfaz para proveedores de datos de ranking.
 * Abstrae el acceso a la información de puntuaciones sin acoplar al origen de datos específico.
 */
public interface RankingDataProvider extends Serializable {
    /**
     * Obtiene la puntuación máxima de un usuario.
     * @param username Nombre del usuario
     * @return Puntuación máxima o 0 si no existe
     */
    int getPuntuacionMaxima(String username);
    
    /**
     * Obtiene la puntuación media de un usuario.
     * @param username Nombre del usuario
     * @return Puntuación media o 0.0 si no existe
     */
    double getPuntuacionMedia(String username);
    
    /**
     * Obtiene el número de partidas jugadas por un usuario.
     * @param username Nombre del usuario
     * @return Número de partidas o 0 si no existe
     */
    int getPartidasJugadas(String username);
    
    /**
     * Obtiene el número de victorias de un usuario.
     * @param username Nombre del usuario
     * @return Número de victorias o 0 si no existe
     */
    int getVictorias(String username);
    
    /**
     * Obtiene la puntuación total acumulada de un usuario.
     * @param username Nombre del usuario
     * @return Puntuación total acumulada o 0 si no existe
     */
    int getPuntuacionTotal(String username);
} 
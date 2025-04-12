package domain.models;

import java.io.Serializable;

/**
 * Interfaz que define las operaciones básicas de un usuario en el sistema.
 */
public interface Usuario extends Serializable {
    
    /**
     * Obtiene el ID del usuario.
     * 
     * @return ID del usuario
     */
    String getId();
    
    /**
     * Verifica si la contraseña proporcionada coincide con la del usuario.
     * 
     * @param password Contraseña a verificar
     * @return true si la contraseña coincide, false en caso contrario
     */
    boolean verificarPassword(String password);
    
    /**
     * Establece una nueva contraseña para el usuario.
     * 
     * @param password Nueva contraseña
     */
    void setPassword(String password);
    
    /**
     * Incrementa el contador de partidas jugadas.
     */
    void incrementarPartidasJugadas();
    
    /**
     * Incrementa el contador de partidas ganadas.
     */
    void incrementarPartidasGanadas();
    
    /**
     * Obtiene el número de partidas jugadas.
     * 
     * @return Número de partidas jugadas
     */
    int getPartidasJugadas();
    
    /**
     * Obtiene el número de partidas ganadas.
     * 
     * @return Número de partidas ganadas
     */
    int getPartidasGanadas();
    
    /**
     * Obtiene el ratio de victorias del usuario.
     * 
     * @return Ratio de victorias (partidas ganadas / partidas jugadas)
     */
    double getRatioVictorias();
    
    /**
     * Verifica si el usuario es una IA.
     * 
     * @return true si es una IA, false si es un jugador humano
     */
    boolean esIA();
}
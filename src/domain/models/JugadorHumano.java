package domain.models;

import java.io.Serializable;

/**
 * Clase que representa a un jugador humano en el sistema.
 * Extiende de Usuario para mantener la información básica.
 */
public class JugadorHumano extends Usuario implements Serializable {
    private int puntuacionUltimaPartida;
    private boolean enPartida;
    
    /**
     * Constructor de la clase JugadorHumano.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     */
    public JugadorHumano(String username, String password) {
        super(username, password);
        this.puntuacionUltimaPartida = 0;
        this.enPartida = false;
    }
    
    /**
     * Constructor de la clase JugadorHumano con información extendida.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param email Correo electrónico
     * @param nombreCompleto Nombre completo del usuario
     */
    public JugadorHumano(String username, String password, String email, String nombreCompleto) {
        super(username, password, email, nombreCompleto);
        this.puntuacionUltimaPartida = 0;
        this.enPartida = false;
    }
    
    /**
     * Establece si el jugador está actualmente en una partida.
     * 
     * @param enPartida true si está en partida, false en caso contrario
     */
    public void setEnPartida(boolean enPartida) {
        this.enPartida = enPartida;
    }
    
    /**
     * Verifica si el jugador está actualmente en una partida.
     * 
     * @return true si está en partida, false en caso contrario
     */
    public boolean isEnPartida() {
        return enPartida;
    }
    
    /**
     * Establece la puntuación de la última partida jugada.
     * 
     * @param puntuacion Puntuación obtenida
     */
    public void setPuntuacionUltimaPartida(int puntuacion) {
        this.puntuacionUltimaPartida = puntuacion;
    }
    
    /**
     * Obtiene la puntuación de la última partida jugada.
     * 
     * @return Puntuación de la última partida
     */
    public int getPuntuacionUltimaPartida() {
        return puntuacionUltimaPartida;
    }
    
    /**
     * Implementación para un jugador humano de cómo jugar su turno.
     * En este caso, es solo un método base que debe ser completado
     * con la interacción real del usuario en la capa de presentación.
     * 
     * @return true si el jugador ha completado su turno, false en caso contrario
     */
    public boolean jugarTurno() {
        // En un jugador humano, este método sería básicamente un placeholder
        // ya que la lógica real estaría en la capa de presentación
        return true;
    }
    
    @Override
    public String toString() {
        return "JugadorHumano{" +
               "username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", nombreCompleto='" + nombreCompleto + '\'' +
               ", partidasJugadas=" + partidasJugadas +
               ", partidasGanadas=" + partidasGanadas +
               ", puntuacionUltimaPartida=" + puntuacionUltimaPartida +
               ", enPartida=" + enPartida +
               '}';
    }
}
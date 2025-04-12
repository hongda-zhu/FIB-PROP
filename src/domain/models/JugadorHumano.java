package domain.models;

import java.io.Serializable;

/**
 * Clase que representa a un jugador humano en el sistema.
 * Extiende de UsuarioBase para mantener la información básica.
 */
public class JugadorHumano extends UsuarioBase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int puntuacionUltimaPartida;
    private boolean enPartida;
    private boolean logueado;
    
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
        this.logueado = false;
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
        this.logueado = false;
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
     * Establece si el jugador está logueado.
     * 
     * @param logueado true si está logueado, false en caso contrario
     */
    public void setLogueado(boolean logueado) {
        this.logueado = logueado;
    }
    
    /**
     * Verifica si el jugador está logueado.
     * 
     * @return true si está logueado, false en caso contrario
     */
    public boolean isLogueado() {
        return logueado;
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
               ", logueado=" + logueado +
               '}';
    }
}
package domain.models;

import java.io.Serializable;

/**
 * Clase que representa a un jugador humano en el sistema.
 */
public class JugadorHumano implements Usuario, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String password;
    private int partidasJugadas;
    private int partidasGanadas;
    private int puntuacionUltimaPartida;
    private boolean enPartida;
    private boolean logueado;
    
    /**
     * Constructor de la clase JugadorHumano.
     * 
     * @param id Identificador único del jugador
     * @param password Contraseña
     */
    public JugadorHumano(String id, String password) {
        this.id = id;
        this.password = password;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
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
    public String getId() {
        return id;
    }
    
    @Override
    public boolean verificarPassword(String passwordToCheck) {
        return this.password.equals(passwordToCheck);
    }
    
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public void incrementarPartidasJugadas() {
        this.partidasJugadas++;
    }
    
    @Override
    public void incrementarPartidasGanadas() {
        this.partidasGanadas++;
    }
    
    @Override
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    @Override
    public int getPartidasGanadas() {
        return partidasGanadas;
    }
    
    @Override
    public double getRatioVictorias() {
        return partidasJugadas > 0 ? (double) partidasGanadas / partidasJugadas : 0.0;
    }
    
    @Override
    public boolean esIA() {
        return false;
    }
    
    @Override
    public String toString() {
        return "JugadorHumano{" +
               "id='" + id + '\'' +
               ", partidasJugadas=" + partidasJugadas +
               ", partidasGanadas=" + partidasGanadas +
               ", puntuacionUltimaPartida=" + puntuacionUltimaPartida +
               ", enPartida=" + enPartida +
               ", logueado=" + logueado +
               '}';
    }
}
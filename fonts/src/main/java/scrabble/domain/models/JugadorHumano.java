package scrabble.domain.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa a un jugador humano en el sistema.
 */
public class JugadorHumano extends Jugador {
    private static final long serialVersionUID = 1L;
    private int puntuacionUltimaPartida;

    private String password;
    private int partidasJugadas;
    private int partidasGanadas;
    private boolean enPartida;
    private boolean logueado;
    

    /**
     * Constructor de la clase JugadorHumano.
     * 
     * @param id Identificador único del jugador
     * @param password Contraseña
     */
    public JugadorHumano(String id, String password) {
        this(id, id, password); // Por defecto, el nombre es igual al ID
        this.puntuacionUltimaPartida = 0;
        this.enPartida = false;
        this.rack = new HashMap<>(); // Inicialmente no tiene fichas en el rack
    }
    
    /**
     * Constructor de la clase JugadorHumano con nombre personalizado.
     * 
     * @param id Identificador único del jugador
     * @param nombre Nombre para mostrar del jugador
     * @param password Contraseña
     */
    public JugadorHumano(String id, String nombre, String password) {
        super(id, nombre);
        this.password = password;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
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
     * Establece el nombre para mostrar del jugador.
     * 
     * @param nombre Nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    /**
     * Establece la puntuación de la última partida jugada.
     * 
     * @param puntuacion Puntuación obtenida
     */
    @Override
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    /**
     * Obtiene la puntuación de la última partida jugada.
     * 
     * @return Puntuación de la última partida
     */
    @Override
    public int getPuntuacion() {
        return puntuacion;
    }
    
    /**
     * Verifica si la contraseña proporcionada coincide con la del jugador.
     * 
     * @param password Contraseña a verificar
     * @return true si la contraseña coincide, false en caso contrario
     */
    public boolean verificarPassword(String passwordToCheck) {
        return this.password.equals(passwordToCheck);
    }
    
    /**
     * Establece una nueva contraseña para el jugador.
     * 
     * @param password Nueva contraseña
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Incrementa el contador de partidas jugadas.
     */
    public void incrementarPartidasJugadas() {
        this.partidasJugadas++;
    }
    
    /**
     * Incrementa el contador de partidas ganadas.
     */
    public void incrementarPartidasGanadas() {
        this.partidasGanadas++;
    }
    
    /**
     * Obtiene el número de partidas jugadas.
     * 
     * @return Número de partidas jugadas
     */
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    /**
     * Obtiene el número de partidas ganadas.
     * 
     * @return Número de partidas ganadas
     */
    public int getPartidasGanadas() {
        return partidasGanadas;
    }
    
    /**
     * Obtiene el ratio de victorias del jugador.
     * 
     * @return Ratio de victorias (partidas ganadas / partidas jugadas)
     */
    public double getRatioVictorias() {
        return partidasJugadas > 0 ? (double) partidasGanadas / partidasJugadas : 0.0;
    }
    
    @Override
    public boolean esIA() {
        return false;
    }


    public Map<String, Integer> getFichas() {
        return rack;
    }

    public int getPuntaje() {
        return puntuacionUltimaPartida;
    }

    public void addPuntaje(int puntos) {
        this.puntuacionUltimaPartida += puntos;
    }

    public void addSkipTrack() {
        this.skipTrack += 1;
    }

    public int getSkipTrack() {
        return skipTrack;
    }

    public void setSkipTrack(int skipTrack) {
        this.skipTrack = skipTrack;
    }

    @Override
    public String toString() {
        return "JugadorHumano{" +
               "id='" + id + '\'' +
               ", nombre='" + nombre + '\'' +
               ", partidasJugadas=" + partidasJugadas +
               ", partidasGanadas=" + partidasGanadas +
               ", puntuacion=" + puntuacion +
               ", enPartida=" + enPartida +
               ", logueado=" + logueado +
               '}';
    }





}
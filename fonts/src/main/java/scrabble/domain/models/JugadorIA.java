package scrabble.domain.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa a un jugador controlado por IA en el sistema.
 * Los jugadores IA se crean para una única partida y no mantienen estadísticas de juego
 * entre sesiones.
 */
public class JugadorIA extends Jugador {
    private static final long serialVersionUID = 1L;
    
    /**
     * Niveles de dificultad para la IA
     */
    public enum Dificultad {
        FACIL, 
        DIFICIL
    }
    
    private Dificultad nivelDificultad;
    private int puntuacionUltimaPartida;
    protected Map<String, Integer> rack;
    protected int skipTrack;

    
    /**
     * Constructor de la clase JugadorIA.
     * 
     * @param id Identificador único para la IA
     * @param dificultad Nivel de dificultad de la IA
     */
    public JugadorIA(String id, Dificultad dificultad) {
        super(id, "IA-" + id);
        this.nivelDificultad = dificultad;
        this.puntuacionUltimaPartida = 0;
        this.rack = new HashMap<>(); // Inicialmente no tiene fichas en el rack
    }
    
    /**
     * Establece el nivel de dificultad de la IA.
     * 
     * @param dificultad Nivel de dificultad
     */
    public void setNivelDificultad(Dificultad dificultad) {
        this.nivelDificultad = dificultad;
    }
    
    /**
     * Obtiene el nivel de dificultad actual de la IA.
     * 
     * @return Nivel de dificultad
     */
    public Dificultad getNivelDificultad() {
        return nivelDificultad;
    }
    
    /**
     * Para simular las estadísticas en ranking, aunque la IA
     * no almacena historial, se considera que ha ganado si tiene
     * puntuación mayor a 0.
     * 
     * @return 1 si ha ganado, 0 si no
     */
    public int getPartidasGanadas() {
        return puntuacion > 0 ? 1 : 0;
    }
    
    /**
     * Para simular estadísticas en rankings.
     * 
     * @return Siempre 1 para IA
     */
    public int getPartidasJugadas() {
        return 1;
    }

    public int getPuntuacionUltimaPartida() {
        return puntuacionUltimaPartida;
    }
    
    public void setPuntuacionUltimaPartida(int puntuacion) {
        this.puntuacionUltimaPartida = puntuacion;
    }

    
    /**
     * Para simular estadísticas en rankings.
     * 
     * @return 0 o 1 en función de si tiene puntuación > 0
     */
    public double getRatioVictorias() {
        return puntuacion > 0 ? 1.0 : 0.0;
    }
    
    @Override
    public boolean esIA() {
        return true;
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
        return "JugadorIA{" +
               "id='" + id + '\'' +
               ", nombre='" + nombre + '\'' +
               ", nivelDificultad=" + nivelDificultad +
               ", puntuacion=" + puntuacion +
               '}';
    }
}
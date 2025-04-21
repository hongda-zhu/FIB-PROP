package scrabble.domain.models;

import java.util.HashMap;
import java.util.Map;

import scrabble.helpers.Dificultad;

/**
 * Clase que representa a un jugador controlado por IA en el sistema.
 * Los jugadores IA se crean para una única partida y no mantienen estadísticas de juego
 * entre sesiones.
 */
public class JugadorIA extends Jugador {
    private static final long serialVersionUID = 1L;
    
    // Contador global para generar nombres únicos para IAs
    private static int contadorIAs = 0;
    
    private Dificultad nivelDificultad;
    private int puntuacionUltimaPartida;

    /**
     * Constructor de la clase JugadorIA.
     * 
     * @param nombre Nombre para identificar la IA (opcional, se genera uno automáticamente)
     * @param dificultad Nivel de dificultad de la IA
     */
    public JugadorIA(String nombre, Dificultad dificultad) {
        super("IA-" + (contadorIAs++));
        this.nivelDificultad = dificultad;
        this.puntuacionUltimaPartida = 0;
        this.rack = new HashMap<>(); // Inicialmente sin fichas en el rack
    }
    
    /**
     * Constructor alternativo que utiliza solo la dificultad y genera un nombre automático.
     * 
     * @param dificultad Nivel de dificultad de la IA
     */
    public JugadorIA(Dificultad dificultad) {
        this(null, dificultad);
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

    /**
     * Obtiene la puntuación de la última partida.
     * 
     * @return Puntuación de la última partida
     */
    public int getPuntuacionUltimaPartida() {
        return puntuacionUltimaPartida;
    }
    
    /**
     * Establece la puntuación de la última partida.
     * 
     * @param puntuacion Nueva puntuación
     */
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
    
    /**
     * Obtiene las fichas del jugador IA.
     * 
     * @return Mapa con las fichas del jugador
     */
    public Map<String, Integer> getFichas() {
        return rack;
    }

    /**
     * Obtiene el puntaje actual.
     * 
     * @return Puntaje actual
     */
    public int getPuntaje() {
        return puntuacionUltimaPartida;
    }

    /**
     * Añade puntos al puntaje actual.
     * 
     * @param puntos Puntos a añadir
     */
    public void addPuntaje(int puntos) {
        this.puntuacionUltimaPartida += puntos;
    }
    
    @Override
    public String toString() {
        return "JugadorIA{" +
               "nombre='" + nombre + '\'' +
               ", nivelDificultad=" + nivelDificultad +
               ", puntuacion=" + puntuacion +
               '}';
    }
}
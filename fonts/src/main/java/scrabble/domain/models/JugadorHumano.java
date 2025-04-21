package scrabble.domain.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa a un jugador humano en el sistema.
 */
public class JugadorHumano extends Jugador {
    private static final long serialVersionUID = 1L;
    private int puntuacionUltimaPartida;
    private int puntuacionTotal;
    private int partidasJugadas;
    private int partidasGanadas;
    private boolean enPartida;

    /**
     * Constructor de la clase JugadorHumano.
     * 
     * @param nombre Nombre del jugador, sirve como identificador único
     */
    public JugadorHumano(String nombre) {
        super(nombre);
        this.puntuacionUltimaPartida = 0;
        this.puntuacionTotal = 0;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
        this.enPartida = false;
        this.rack = new HashMap<>(); // Inicialmente sin fichas en el rack
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

    /**
     * Obtiene las fichas del jugador.
     * 
     * @return Mapa con las fichas del jugador
     */
    public Map<String, Integer> getFichas() {
        return rack;
    }

    /**
     * Obtiene el puntaje de la última partida.
     * 
     * @return Puntaje de la última partida
     */
    public int getPuntaje() {
        return puntuacionUltimaPartida;
    }

    /**
     * Añade puntos al puntaje de la última partida.
     * 
     * @param puntos Puntos a añadir
     */
    public void addPuntaje(int puntos) {
        this.puntuacionUltimaPartida += puntos;
    }

    /**
     * Obtiene la puntuación total acumulada de todas las partidas.
     * 
     * @return Puntuación total acumulada
     */
    public int getPuntuacionTotal() {
        return puntuacionTotal;
    }
    
    /**
     * Establece la puntuación total acumulada.
     * 
     * @param puntuacionTotal Nueva puntuación total
     */
    public void setPuntuacionTotal(int puntuacionTotal) {
        this.puntuacionTotal = puntuacionTotal;
    }
    
    /**
     * Añade puntos a la puntuación total acumulada.
     * 
     * @param puntos Puntos a añadir
     */
    public void addPuntuacionTotal(int puntos) {
        this.puntuacionTotal += puntos;
    }

    @Override
    public String toString() {
        return "JugadorHumano{" +
               "nombre='" + nombre + '\'' +
               ", partidasJugadas=" + partidasJugadas +
               ", partidasGanadas=" + partidasGanadas +
               ", puntuacion=" + puntuacion +
               ", puntuacionTotal=" + puntuacionTotal +
               ", enPartida=" + enPartida +
               '}';
    }
}
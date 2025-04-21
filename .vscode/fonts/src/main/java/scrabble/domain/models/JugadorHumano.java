package scrabble.domain.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa a un jugador humano en el sistema.
 */
public class JugadorHumano extends Jugador {
    private static final long serialVersionUID = 1L;
    private boolean enPartida;
    private String nombrePartidaActual; // Nombre de la partida en la que está participando

    /**
     * Constructor de la clase JugadorHumano.
     * 
     * @param nombre Nombre del jugador, sirve como identificador único
     */
    public JugadorHumano(String nombre) {
        super(nombre);
        this.enPartida = false;
        this.nombrePartidaActual = ""; // Inicialmente sin partida asignada
        this.rack = new HashMap<>(); // Inicialmente sin fichas en el rack
    }
    
    /**
     * Establece si el jugador está actualmente en una partida.
     * 
     * @param enPartida true si está en partida, false en caso contrario
     */
    public void setEnPartida(boolean enPartida) {
        this.enPartida = enPartida;
        
        // Si ya no está en partida, limpiamos el nombre de la partida
        if (!enPartida) {
            this.nombrePartidaActual = "";
        }
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
     * Obtiene el nombre de la partida en la que está participando el jugador.
     * 
     * @return Nombre de la partida actual o cadena vacía si no está en partida
     */
    public String getNombrePartidaActual() {
        return nombrePartidaActual;
    }

    /**
     * Establece el nombre de la partida en la que está participando el jugador.
     * 
     * @param nombrePartida Nombre de la partida
     */
    public void setNombrePartidaActual(String nombrePartida) {
        this.nombrePartidaActual = nombrePartida;
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

    @Override
    public String toString() {
        return "JugadorHumano{" +
               "nombre='" + nombre + '\'' +
               ", enPartida=" + enPartida +
               ", nombrePartidaActual='" + nombrePartidaActual + '\'' +
               '}';
    }
}
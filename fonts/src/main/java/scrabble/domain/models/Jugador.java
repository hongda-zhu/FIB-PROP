package scrabble.domain.models;

import java.io.Serializable;
import java.util.Map;

import scrabble.helpers.Tuple;

/**
 * Clase abstracta que define las operaciones y atributos básicos comunes a todos los jugadores.
 */
public abstract class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    public Map<String, Integer> rack; 
    public int skipTrack;
    
    /**
     * Identificador único del jugador
     */
    protected String id;
    
    /**
     * Nombre para mostrar del jugador
     */
    protected String nombre;
    
    /**
     * Puntuación del jugador
     */
    protected int puntuacion;
    
    /**
     * Constructor para la clase Jugador
     * 
     * @param id Identificador único del jugador
     * @param nombre Nombre para mostrar
     */
    public Jugador(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.puntuacion = 0;
        this.skipTrack = 0;
        this.rack = null; 
    }
    
    /**
     * Obtiene el ID del jugador.
     * 
     * @return ID del jugador
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtiene el nombre para mostrar del jugador.
     * 
     * @return Nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Establece la puntuación del jugador.
     * 
     * @param puntuacion Nueva puntuación
     */
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    /**
     * Obtiene la puntuación actual del jugador.
     * 
     * @return Puntuación actual
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    public void addPuntuacion(int puntuacion) {
        this.puntuacion += puntuacion;
    }

    public void inicializarRack (Map<String, Integer> rack) {
        this.rack = rack;
    }

    public Map<String, Integer> getRack() {
        return rack;
    }

    public Tuple<String, Integer> sacarFicha(String ficha) {
        if (rack.containsKey(ficha)) {
            int cantidad = rack.get(ficha);
            if (cantidad > 1) {
                rack.put(ficha, cantidad - 1);
            } else {
                rack.remove(ficha);
            }
            return new Tuple<>(ficha, cantidad - 1);
        }
        return null; 
    }

    public void agregarFicha(String ficha) {
        if (rack.containsKey(ficha)) {
            int cantidad = rack.get(ficha);
            rack.put(ficha, cantidad + 1);
        } else {
            rack.put(ficha, 1);
        }
    }

    public int getCantidadFichas() {
        return rack.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void addSkipTrack() {
        this.skipTrack += 1;
    }

    public void setSkipTrack(int skipTrack) {
        this.skipTrack = skipTrack;
    }

    public int getSkipTrack() {
        return skipTrack;
    }

    /**
     * Verifica si el jugador es una IA.
     * Cada clase hija debe implementar este método.
     * 
     * @return true si es una IA, false si es un jugador humano
     */
    public abstract boolean esIA();
} 
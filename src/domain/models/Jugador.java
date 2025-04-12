package domain.models;

import java.io.Serializable;

/**
 * Clase abstracta que define las operaciones y atributos básicos comunes a todos los jugadores.
 */
public abstract class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;
    
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
    
    /**
     * Verifica si el jugador es una IA.
     * Cada clase hija debe implementar este método.
     * 
     * @return true si es una IA, false si es un jugador humano
     */
    public abstract boolean esIA();
} 
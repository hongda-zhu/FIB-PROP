package domain.models;

import java.io.Serializable;

/**
 * Clase que representa a un jugador controlado por IA en el sistema.
 * Los jugadores IA se crean para una única partida y no mantienen estadísticas de juego
 * entre sesiones.
 */
public class JugadorIA implements Usuario, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Niveles de dificultad para la IA
     */
    public enum Dificultad {
        FACIL, 
        MEDIO,
        DIFICIL
    }
    
    private String id;
    private Dificultad nivelDificultad;
    private int puntuacion;
    
    /**
     * Constructor de la clase JugadorIA.
     * 
     * @param id Identificador único para la IA
     * @param dificultad Nivel de dificultad de la IA
     */
    public JugadorIA(String id, Dificultad dificultad) {
        this.id = id;
        this.nivelDificultad = dificultad;
        this.puntuacion = 0;
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
     * Establece la puntuación de la IA.
     * 
     * @param puntuacion Puntuación obtenida
     */
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    /**
     * Obtiene la puntuación de la IA.
     * 
     * @return Puntuación
     */
    public int getPuntuacion() {
        return puntuacion;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public boolean verificarPassword(String passwordToCheck) {
        // Los jugadores IA no tienen contraseña
        return true;
    }
    
    @Override
    public void setPassword(String password) {
        // No hacer nada, los jugadores IA no tienen contraseña
    }
    
    @Override
    public void incrementarPartidasJugadas() {
        // No aplicable para IA
    }
    
    @Override
    public void incrementarPartidasGanadas() {
        // No aplicable para IA
    }
    
    @Override
    public int getPartidasJugadas() {
        // Siempre es 1 para IA
        return 1;
    }
    
    @Override
    public int getPartidasGanadas() {
        // 0 o 1 según si ganó
        return puntuacion > 0 ? 1 : 0;
    }
    
    @Override
    public double getRatioVictorias() {
        // Siempre 0 o 1 para IA
        return puntuacion > 0 ? 1.0 : 0.0;
    }
    
    @Override
    public boolean esIA() {
        return true;
    }
    
    @Override
    public String toString() {
        return "JugadorIA{" +
               "id='" + id + '\'' +
               ", nivelDificultad=" + nivelDificultad +
               ", puntuacion=" + puntuacion +
               '}';
    }
}
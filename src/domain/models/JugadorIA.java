package domain.models;

import java.io.Serializable;
import java.util.List;

/**
 * Clase que representa a un jugador controlado por IA en el sistema.
 * Extiende de Usuario para mantener la información básica.
 */
public class JugadorIA extends Usuario implements Serializable {
    // Niveles de dificultad para la IA
    public enum Dificultad {
        FACIL, DIFICIL
    }
    
    private Dificultad nivelDificultad;
    private int puntuacionUltimaPartida;
    
    /**
     * Constructor de la clase JugadorIA.
     * 
     * @param username Nombre de usuario para la IA
     * @param dificultad Nivel de dificultad de la IA
     */
    public JugadorIA(String username, Dificultad dificultad) {
        super(username, "ia_password"); // La IA no necesita contraseña real
        this.nivelDificultad = dificultad;
        this.puntuacionUltimaPartida = 0;
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
    
    /**
     * Implementación para un jugador IA de cómo jugar su turno.
     * Este método contiene la lógica principal que determina la jugada de la IA.
     * 
     * @param tablero Tablero actual del juego
     * @param fichasDisponibles Fichas disponibles para la IA
     * @return Una representación de la jugada elegida por la IA
     */
    public Object jugarTurno(Object tablero, List<Character> fichasDisponibles) {
        // Aquí iría la lógica de decisión de la IA según su nivel de dificultad
        // Por ahora es un placeholder
        
        switch (nivelDificultad) {
            case FACIL:
                // Lógica simple: buscar cualquier palabra válida
                break;
            case DIFICIL:
                // Lógica avanzada: maximizar puntuación y posición estratégica
                break;
        }
        
        return null; // Placeholder
    }
    
    @Override
    public String toString() {
        return "JugadorIA{" +
               "username='" + username + '\'' +
               ", nivelDificultad=" + nivelDificultad +
               ", partidasJugadas=" + partidasJugadas +
               ", partidasGanadas=" + partidasGanadas +
               ", puntuacionUltimaPartida=" + puntuacionUltimaPartida +
               '}';
    }
}
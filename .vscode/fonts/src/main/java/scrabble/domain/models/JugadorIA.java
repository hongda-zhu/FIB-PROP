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

    /**
     * Constructor de la clase JugadorIA.
     * 
     * @param nombre Nombre para identificar la IA (opcional, se genera uno automáticamente)
     * @param dificultad Nivel de dificultad de la IA
     */
    public JugadorIA(String nombre, Dificultad dificultad) {
        // Nombre con formato IA_Dificultad_Num para mejor identificación
        super(nombre != null ? nombre : generarNombreIA(dificultad));
        this.nivelDificultad = dificultad;
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
     * Genera un nombre para la IA basado en su dificultad.
     * 
     * @param dificultad Nivel de dificultad de la IA
     * @return Nombre generado
     */
    private static String generarNombreIA(Dificultad dificultad) {
        String dificultadStr = dificultad == Dificultad.FACIL ? "FACIL" : "DIFICIL";
        return "IA_" + dificultadStr + "_" + (contadorIAs++);
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
     * Para simular estadísticas en rankings.
     * 
     * @return Siempre 1 para IA
     */
    public int getPartidasJugadas() {
        return 1;
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
    
    @Override
    public String toString() {
        return "JugadorIA{" +
               "nombre='" + nombre + '\'' +
               ", nivelDificultad=" + nivelDificultad +
               '}';
    }
}
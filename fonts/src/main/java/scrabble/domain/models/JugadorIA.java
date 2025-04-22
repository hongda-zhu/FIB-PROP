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
     * @pre dificultad no puede ser null
     * @post Se crea una instancia de JugadorIA con el nombre dado o uno generado automáticamente
     * @throws NullPointerException si dificultad es null
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
     * @pre dificultad no puede ser null
     * @post Se crea una instancia de JugadorIA con un nombre generado automáticamente
     * @throws NullPointerException si dificultad es null
     */
    public JugadorIA(Dificultad dificultad) {
        this(null, dificultad);
    }
    
    /**
     * Genera un nombre para la IA basado en su dificultad.
     * 
     * @param dificultad Nivel de dificultad de la IA
     * @pre dificultad no puede ser null
     * @return Nombre generado con formato "IA_[DIFICULTAD]_[CONTADOR]"
     * @post El contador global de IAs se incrementa en 1
     * @throws NullPointerException si dificultad es null
     */
    private static String generarNombreIA(Dificultad dificultad) {
        String dificultadStr = dificultad == Dificultad.FACIL ? "FACIL" : "DIFICIL";
        return "IA_" + dificultadStr + "_" + (contadorIAs++);
    }
    
    /**
     * Establece el nivel de dificultad de la IA.
     * 
     * @param dificultad Nivel de dificultad
     * @pre dificultad no puede ser null
     * @post El nivel de dificultad de la IA queda establecido al valor proporcionado
     * @throws NullPointerException si dificultad es null
     */
    public void setNivelDificultad(Dificultad dificultad) {
        this.nivelDificultad = dificultad;
    }
    
    /**
     * Obtiene el nivel de dificultad actual de la IA.
     * 
     * @return Nivel de dificultad
     * @pre No hay precondiciones específicas
     * @post Se devuelve el nivel de dificultad actual de la IA
     */
    public Dificultad getNivelDificultad() {
        return nivelDificultad;
    }
    
    /**
     * Obtiene el número de partidas jugadas por la IA.
     * Para simular estadísticas en rankings.
     * 
     * @return Siempre 1 para IA
     * @pre No hay precondiciones específicas
     * @post Se devuelve siempre 1, ya que las IAs solo existen para una partida
     */
    public int getPartidasJugadas() {
        return 1;
    }
    
    /**
     * Indica si este jugador es controlado por IA.
     * 
     * @return true, ya que esta clase representa un jugador IA
     * @pre No hay precondiciones específicas
     * @post Se devuelve siempre true
     */
    @Override
    public boolean esIA() {
        return true;
    }
    
    /**
     * Obtiene las fichas del jugador IA.
     * 
     * @return Mapa con las fichas del jugador
     * @pre No hay precondiciones específicas
     * @post Se devuelve el rack actual de fichas del jugador IA
     */
    public Map<String, Integer> getFichas() {
        return rack;
    }
    
    /**
     * Devuelve una representación en forma de String del jugador IA.
     * 
     * @return String con la información del jugador IA
     * @pre No hay precondiciones específicas
     * @post Se devuelve una cadena con el formato "JugadorIA{nombre='[nombre]', nivelDificultad=[dificultad]}"
     */
    @Override
    public String toString() {
        return "JugadorIA{" +
               "nombre='" + nombre + '\'' +
               ", nivelDificultad=" + nivelDificultad +
               '}';
    }
}